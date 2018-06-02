package me.onenrico.company.utils;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.onenrico.company.locale.Locales;
import me.onenrico.company.main.Core;
import me.onenrico.company.nms.actionbar.ActionBar;
import me.onenrico.company.nms.titlebar.TitleBar;

public class MessageUT {
	public static String centered(String message) {
		message = t(message);
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = 154 - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return (sb.toString() + message);
	}

	public static String t(String colorize) {
		return ChatColor.translateAlternateColorCodes('&', colorize);
	}

	public static String u(String decolorize) {
		return decolorize.replace('§', '&');
	}

	public static String d(String remove) {
		remove = MessageUT.t(remove);
		for (ChatColor color : ChatColor.values()) {
			remove = remove.replaceAll(color.toString(), "");
		}
		return remove;
	}

	public static void pmessage(Player player, List<String> messages) {
		for (String m : messages) {
			pmessage(player, m);
		}
	}

	public static void plmessage(Player player, List<String> messages, Boolean warning) {
		for (String m : messages) {
			if(player == null) {
				Core.getThis().getLogger().info(d(m));
			}else {
				plmessage(player, m, warning);
			}
		}
	}

	public static void plmessage(Player player, List<String> messages) {
		plmessage(player, messages, false);
	}

	public static void cmessage(String teks) {
		Core.getThis().getServer().getConsoleSender().sendMessage(t(teks));
	}

	public static void cmessage(List<String> teks) {
		for (String tek : teks) {
			cmessage(MessageUT.t(tek));
		}
	}

	public static void debug(String o) {
		Core.getThis().getLogger().info(t("<Debug> " + o));
	}

	public static void debug(Player player, String o) {
		pmessage(player, "&8[&dDebug&8] &f" + o);
	}

	// PLAYER MESSAGE
	public static void pmessage(Player player, String teks) {
		pmessage(player, teks, false);
	}

	public static void pmessage(Player player, String teks, Boolean Action) {
		PlaceholderUT pu = new PlaceholderUT(Locales.getPlaceholder());
		teks = pu.t(teks);
		player.sendMessage(teks);
		if (Action) {
			acmessage(player, teks);
		}
	}

	public static void plmessage(Player player, String teks) {
		plmessage(player, teks, false);
	}

	public static void plmessage(Player player, String teks, Boolean warning) {
		plmessage(player, teks, warning, false);
	}

	public static void plmessage(Player player, String teks, Boolean warning, Boolean Action) {
		String pref = Locales.pluginPrefix;
		if (teks.contains("<np>")) {
			pref = "";
			teks = teks.replace("<np>", "");
		}
		if (warning) {
			if (teks.contains("<title>")) {
				teks = teks.replace("<title>", "");
				String title = teks;
				if (teks.contains("<subtitle>")) {
					title = teks.split("<subtitle>")[0];
					String subtitle = teks.split("<subtitle>")[1];
					tfullmessage(player, teks, subtitle);
				} else {

					ttmessage(player, title);
				}
				return;
			} else if (teks.contains("<subtitle>")) {
				teks = teks.replaceAll("<subtitle>", "");
				tsubmessage(player, teks);
				return;
			}
			if (teks.contains("<center>")) {
				teks = teks.replace("<center>", "");
				pmessage(player, centered(pref + "&c" + teks), Action);
			} else {
				pmessage(player, pref + "&c" + teks, Action);
			}
		} else {
			if (teks.contains("<action>")) {
				teks = teks.replace("<action>", "");
				acmessage(player, teks);
				return;
			} else if (teks.contains("<title>")) {
				teks = teks.replace("<title>", "");
				String title = teks;
				if (teks.contains("<subtitle>")) {
					title = teks.split("<subtitle>")[0];
					String subtitle = teks.split("<subtitle>")[1];
					tfullmessage(player, title, subtitle);
				} else {
					ttmessage(player, title);
				}
				return;
			} else if (teks.contains("<subtitle>")) {
				teks = teks.replaceAll("<subtitle>", "");
				tsubmessage(player, teks);
				return;
			}
			if (teks.contains("<center>")) {
				teks = teks.replace("<center>", "");
				pmessage(player, centered(pref + "&b" + teks), Action);
			} else {
				pmessage(player, pref + "&b" + teks, Action);
			}
		}
	}
	// PLAYER MESSAGE

	// ACTIONBAR MESSAGE
	public static void acplmessage(Player player, String teks, Boolean warning) {
		String pref = Locales.pluginPrefix;
		if (teks.contains("<np>")) {
			teks = teks.replace("<np>", "");
			pref = "";
		}
		teks = teks.replace("<center>", "");
		if (warning) {
			acmessage(player, pref + "&c" + teks);
		} else {
			acmessage(player, pref + "&b" + teks);
		}
	}

	public static void acplmessage(Player player, String teks) {
		acplmessage(player, teks, false);
	}

	public static void acplmessage(Player player, List<String> teks) {
		acplmessage(player, teks, false);
	}

	public static void acplmessage(Player player, List<String> messages, Boolean warning) {
		for (String m : messages) {
			acplmessage(player, m, warning);
		}
	}

	public static void acmessage(Player player, String teks) {
		teks = Locales.pub.t(teks);
		ActionBar.sendActionBar(player, teks);
	}
	// ACTIONBAR MESSAGE

	// TITLEBAR MESSAGE
	public static void tfullmessage(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
		TitleBar.sendTitle(player, fadein, stay, fadeout, Locales.pub.t(title), Locales.pub.t(subtitle));
	}

	public static void tfullmessage(Player player, String title, String subtitle) {
		tfullmessage(player, title, subtitle, 20, 60, 20);
	}

	public static void tsubmessage(Player player, String subtitle, int fadein, int stay, int fadeout) {
		tfullmessage(player, "", subtitle, fadein, stay, fadeout);
	}

	public static void tsubmessage(Player player, String subtitle) {
		tfullmessage(player, "", subtitle, 20, 60, 20);
	}

	public static void ttmessage(Player player, String title, int fadein, int stay, int fadeout) {
		tfullmessage(player, title, "", fadein, stay, fadeout);
	}

	public static void ttmessage(Player player, String title) {
		tfullmessage(player, "", title, 20, 60, 20);
	}

	// TITLEBAR MESSAGE
	public enum DefaultFontInfo {
		A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5), F(
				'F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j',
						5), K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O(
								'O',
								5), o('o', 5), P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S',
										5), s('s', 5), T('T', 5), t('t', 4), U('U', 5), u('u', 5), V('V', 5), v('v',
												5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5), Z(
														'Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3',
																5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5), NUM_7(
																		'7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0(
																				'0', 5), EXCLAMATION_POINT('!',
																						1), AT_SYMBOL('@', 6), NUM_SIGN(
																								'#',
																								5), DOLLAR_SIGN('$',
																										5), PERCENT('%',
																												5), UP_ARROW(
																														'^',
																														5), AMPERSAND(
																																'&',
																																5), ASTERISK(
																																		'*',
																																		5), LEFT_PARENTHESIS(
																																				'(',
																																				4), RIGHT_PERENTHESIS(
																																						')',
																																						4), MINUS(
																																								'-',
																																								5), UNDERSCORE(
																																										'_',
																																										5), PLUS_SIGN(
																																												'+',
																																												5), EQUALS_SIGN(
																																														'=',
																																														5), LEFT_CURL_BRACE(
																																																'{',
																																																4), RIGHT_CURL_BRACE(
																																																		'}',
																																																		4), LEFT_BRACKET(
																																																				'[',
																																																				3), RIGHT_BRACKET(
																																																						']',
																																																						3), COLON(
																																																								':',
																																																								1), SEMI_COLON(
																																																										';',
																																																										1), DOUBLE_QUOTE(
																																																												'"',
																																																												3), SINGLE_QUOTE(
																																																														'\'',
																																																														1), LEFT_ARROW(
																																																																'<',
																																																																4), RIGHT_ARROW(
																																																																		'>',
																																																																		4), QUESTION_MARK(
																																																																				'?',
																																																																				5), SLASH(
																																																																						'/',
																																																																						5), BACK_SLASH(
																																																																								'\\',
																																																																								5), LINE(
																																																																										'|',
																																																																										1), TILDE(
																																																																												'~',
																																																																												5), TICK(
																																																																														'`',
																																																																														2), PERIOD(
																																																																																'.',
																																																																																1), COMMA(
																																																																																		',',
																																																																																		1), SPACE(
																																																																																				' ',
																																																																																				3), DEFAULT(
																																																																																						'a',
																																																																																						4);
		private char character;
		private int length;

		DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}

		public char getCharacter() {
			return character;
		}

		public int getLength() {
			return length;
		}

		public int getBoldLength() {
			if (this == DefaultFontInfo.SPACE) {
				return getLength();
			}
			return length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c) {
			for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
				if (dFI.getCharacter() == c) {
					return dFI;
				}
			}
			return DefaultFontInfo.DEFAULT;
		}
	}
}
