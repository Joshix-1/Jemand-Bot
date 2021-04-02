package Jemand;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class Brainfuck {
        private  int ptr;
        private  int length = 65535;
        private  byte[] memory = new byte[length];
        private String s;
        private MessageCreateEvent e;
        private int ici = 0;
        AtomicReferenceArray<String> input = new AtomicReferenceArray<String>(9999);

        public Brainfuck() {
        }

        public final String interpret(String s, String input) {
            StringBuilder returnvalue = new StringBuilder();
            int c = 0;
            char[] ic = input.toCharArray();
            int ici = 0;

            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '>') {
                    if (ptr == length - 1) ptr = 0;
                    else ptr ++;
                } else if (s.charAt(i) == '<') {
                    if (ptr == 0) ptr = length - 1;
                    else ptr --;
                } else if (s.charAt(i) == '+') memory[ptr] ++;
                else if (s.charAt(i) == '-') memory[ptr] --;
                else if (s.charAt(i) == '.') returnvalue.append((char) (memory[ptr]));
                else if (s.charAt(i) == ','){
                    memory[ptr] = (byte) ic[ici];
                    if(ici+1 < ic.length) ici++;
                    else ici = 0;
                }
                else if (s.charAt(i) == '[') {
                    if (memory[ptr] == 0) {
                        i++;
                        while (c > 0 || s.charAt(i) != ']') {
                            if (s.charAt(i) == '[') c++;
                            else if (s.charAt(i) == ']') c--;
                            i ++;
                        }
                    }
                } else if (s.charAt(i) == ']') {
                    if (memory[ptr] != 0) {
                        i --;
                        while (c > 0 || s.charAt(i) != '[') {
                            if (s.charAt(i) == ']') c ++;
                            else if (s.charAt(i) == '[') c --;
                            i --;
                        }
                        i --;
                    }
                }
            }
            return returnvalue.toString();
        }
    }
