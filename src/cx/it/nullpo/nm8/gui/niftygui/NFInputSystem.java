package cx.it.nullpo.nm8.gui.niftygui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMouseListener;
import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.spi.input.InputSystem;

public class NFInputSystem implements InputSystem, NFKeyListener, NFMouseListener {
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(NFInputSystem.class);

	/** AWT->NiftyGUI keycode table. It's HUGE, and lots of keys are unsupported. */
	public static final int[] KEYTABLE =
	{
		KeyboardInputEvent.KEY_NONE,		// VK_UNDEFINED
		KeyboardInputEvent.KEY_NONE,		// (1)
		KeyboardInputEvent.KEY_NONE,		// (2)
		KeyboardInputEvent.KEY_NONE,		// VK_CANCEL
		KeyboardInputEvent.KEY_NONE,		// (4)
		KeyboardInputEvent.KEY_NONE,		// (5)
		KeyboardInputEvent.KEY_NONE,		// (6)
		KeyboardInputEvent.KEY_NONE,		// (7)
		KeyboardInputEvent.KEY_BACK,		// VK_BACK_SPACE
		KeyboardInputEvent.KEY_TAB,		// VK_TAB
		KeyboardInputEvent.KEY_RETURN,		// VK_ENTER
		KeyboardInputEvent.KEY_NONE,		// (11)
		KeyboardInputEvent.KEY_NONE,		// VK_CLEAR
		KeyboardInputEvent.KEY_NONE,		// (13)
		KeyboardInputEvent.KEY_NONE,		// (14)
		KeyboardInputEvent.KEY_NONE,		// (15)
		KeyboardInputEvent.KEY_LSHIFT,		// VK_SHIFT
		KeyboardInputEvent.KEY_LCONTROL,		// VK_CONTROL
		KeyboardInputEvent.KEY_LMETA,		// VK_ALT
		KeyboardInputEvent.KEY_PAUSE,		// VK_PAUSE
		KeyboardInputEvent.KEY_CAPITAL,		// VK_CAPS_LOCK
		KeyboardInputEvent.KEY_KANA,		// VK_KANA
		KeyboardInputEvent.KEY_NONE,		// (22)
		KeyboardInputEvent.KEY_NONE,		// (23)
		KeyboardInputEvent.KEY_NONE,		// VK_FINAL
		KeyboardInputEvent.KEY_KANJI,		// VK_KANJI
		KeyboardInputEvent.KEY_NONE,		// (26)
		KeyboardInputEvent.KEY_ESCAPE,		// VK_ESCAPE
		KeyboardInputEvent.KEY_CONVERT,		// VK_CONVERT
		KeyboardInputEvent.KEY_NOCONVERT,		// VK_NONCONVERT
		KeyboardInputEvent.KEY_NONE,		// VK_ACCEPT
		KeyboardInputEvent.KEY_NONE,		// VK_MODECHANGE
		KeyboardInputEvent.KEY_SPACE,		// VK_SPACE
		KeyboardInputEvent.KEY_PRIOR,		// VK_PAGE_UP
		KeyboardInputEvent.KEY_NEXT,		// VK_PAGE_DOWN
		KeyboardInputEvent.KEY_END,		// VK_END
		KeyboardInputEvent.KEY_HOME,		// VK_HOME
		KeyboardInputEvent.KEY_LEFT,		// VK_LEFT
		KeyboardInputEvent.KEY_UP,		// VK_UP
		KeyboardInputEvent.KEY_RIGHT,		// VK_RIGHT
		KeyboardInputEvent.KEY_DOWN,		// VK_DOWN
		KeyboardInputEvent.KEY_NONE,		// (41)
		KeyboardInputEvent.KEY_NONE,		// (42)
		KeyboardInputEvent.KEY_NONE,		// (43)
		KeyboardInputEvent.KEY_COMMA,		// VK_COMMA
		KeyboardInputEvent.KEY_MINUS,		// VK_MINUS
		KeyboardInputEvent.KEY_PERIOD,		// VK_PERIOD
		KeyboardInputEvent.KEY_SLASH,		// VK_SLASH
		KeyboardInputEvent.KEY_0,		// VK_0
		KeyboardInputEvent.KEY_1,		// VK_1
		KeyboardInputEvent.KEY_2,		// VK_2
		KeyboardInputEvent.KEY_3,		// VK_3
		KeyboardInputEvent.KEY_4,		// VK_4
		KeyboardInputEvent.KEY_5,		// VK_5
		KeyboardInputEvent.KEY_6,		// VK_6
		KeyboardInputEvent.KEY_7,		// VK_7
		KeyboardInputEvent.KEY_8,		// VK_8
		KeyboardInputEvent.KEY_9,		// VK_9
		KeyboardInputEvent.KEY_NONE,		// (58)
		KeyboardInputEvent.KEY_SEMICOLON,		// VK_SEMICOLON
		KeyboardInputEvent.KEY_NONE,		// (60)
		KeyboardInputEvent.KEY_EQUALS,		// VK_EQUALS
		KeyboardInputEvent.KEY_NONE,		// (62)
		KeyboardInputEvent.KEY_NONE,		// (63)
		KeyboardInputEvent.KEY_NONE,		// (64)
		KeyboardInputEvent.KEY_A,		// VK_A
		KeyboardInputEvent.KEY_B,		// VK_B
		KeyboardInputEvent.KEY_C,		// VK_C
		KeyboardInputEvent.KEY_D,		// VK_D
		KeyboardInputEvent.KEY_E,		// VK_E
		KeyboardInputEvent.KEY_F,		// VK_F
		KeyboardInputEvent.KEY_G,		// VK_G
		KeyboardInputEvent.KEY_H,		// VK_H
		KeyboardInputEvent.KEY_I,		// VK_I
		KeyboardInputEvent.KEY_J,		// VK_J
		KeyboardInputEvent.KEY_K,		// VK_K
		KeyboardInputEvent.KEY_L,		// VK_L
		KeyboardInputEvent.KEY_M,		// VK_M
		KeyboardInputEvent.KEY_N,		// VK_N
		KeyboardInputEvent.KEY_O,		// VK_O
		KeyboardInputEvent.KEY_P,		// VK_P
		KeyboardInputEvent.KEY_Q,		// VK_Q
		KeyboardInputEvent.KEY_R,		// VK_R
		KeyboardInputEvent.KEY_S,		// VK_S
		KeyboardInputEvent.KEY_T,		// VK_T
		KeyboardInputEvent.KEY_U,		// VK_U
		KeyboardInputEvent.KEY_V,		// VK_V
		KeyboardInputEvent.KEY_W,		// VK_W
		KeyboardInputEvent.KEY_X,		// VK_X
		KeyboardInputEvent.KEY_Y,		// VK_Y
		KeyboardInputEvent.KEY_Z,		// VK_Z
		KeyboardInputEvent.KEY_LBRACKET,		// VK_OPEN_BRACKET
		KeyboardInputEvent.KEY_BACKSLASH,		// VK_BACK_SLASH
		KeyboardInputEvent.KEY_RBRACKET,		// VK_CLOSE_BRACKET
		KeyboardInputEvent.KEY_NONE,		// (94)
		KeyboardInputEvent.KEY_NONE,		// (95)
		KeyboardInputEvent.KEY_NUMPAD0,		// VK_NUMPAD0
		KeyboardInputEvent.KEY_NUMPAD1,		// VK_NUMPAD1
		KeyboardInputEvent.KEY_NUMPAD2,		// VK_NUMPAD2
		KeyboardInputEvent.KEY_NUMPAD3,		// VK_NUMPAD3
		KeyboardInputEvent.KEY_NUMPAD4,		// VK_NUMPAD4
		KeyboardInputEvent.KEY_NUMPAD5,		// VK_NUMPAD5
		KeyboardInputEvent.KEY_NUMPAD6,		// VK_NUMPAD6
		KeyboardInputEvent.KEY_NUMPAD7,		// VK_NUMPAD7
		KeyboardInputEvent.KEY_NUMPAD8,		// VK_NUMPAD8
		KeyboardInputEvent.KEY_NUMPAD9,		// VK_NUMPAD9
		KeyboardInputEvent.KEY_MULTIPLY,		// VK_MULTIPLY
		KeyboardInputEvent.KEY_ADD,		// VK_ADD
		KeyboardInputEvent.KEY_NONE,		// VK_SEPARATOR
		KeyboardInputEvent.KEY_SUBTRACT,		// VK_SUBTRACT
		KeyboardInputEvent.KEY_DECIMAL,		// VK_DECIMAL
		KeyboardInputEvent.KEY_DIVIDE,		// VK_DIVIDE
		KeyboardInputEvent.KEY_F1,		// VK_F1
		KeyboardInputEvent.KEY_F2,		// VK_F2
		KeyboardInputEvent.KEY_F3,		// VK_F3
		KeyboardInputEvent.KEY_F4,		// VK_F4
		KeyboardInputEvent.KEY_F5,		// VK_F5
		KeyboardInputEvent.KEY_F6,		// VK_F6
		KeyboardInputEvent.KEY_F7,		// VK_F7
		KeyboardInputEvent.KEY_F8,		// VK_F8
		KeyboardInputEvent.KEY_F9,		// VK_F9
		KeyboardInputEvent.KEY_F10,		// VK_F10
		KeyboardInputEvent.KEY_F11,		// VK_F11
		KeyboardInputEvent.KEY_F12,		// VK_F12
		KeyboardInputEvent.KEY_NONE,		// (124)
		KeyboardInputEvent.KEY_NONE,		// (125)
		KeyboardInputEvent.KEY_NONE,		// (126)
		KeyboardInputEvent.KEY_DELETE,		// VK_DELETE
		KeyboardInputEvent.KEY_GRAVE,		// VK_DEAD_GRAVE
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_ACUTE
		KeyboardInputEvent.KEY_CIRCUMFLEX,		// VK_DEAD_CIRCUMFLEX
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_TILDE
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_MACRON
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_BREVE
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_ABOVEDOT
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_DIAERESIS
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_ABOVERING
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_DOUBLEACUTE
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_CARON
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_CEDILLA
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_OGONEK
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_IOTA
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_VOICED_SOUND
		KeyboardInputEvent.KEY_NONE,		// VK_DEAD_SEMIVOICED_SOUND
		KeyboardInputEvent.KEY_NUMLOCK,		// VK_NUM_LOCK
		KeyboardInputEvent.KEY_SCROLL,		// VK_SCROLL_LOCK
		KeyboardInputEvent.KEY_NONE,		// (146)
		KeyboardInputEvent.KEY_NONE,		// (147)
		KeyboardInputEvent.KEY_NONE,		// (148)
		KeyboardInputEvent.KEY_NONE,		// (149)
		KeyboardInputEvent.KEY_NONE,		// VK_AMPERSAND
		KeyboardInputEvent.KEY_NONE,		// VK_ASTERISK
		KeyboardInputEvent.KEY_NONE,		// VK_QUOTEDBL
		KeyboardInputEvent.KEY_NONE,		// VK_LESS
		KeyboardInputEvent.KEY_SYSRQ,		// VK_PRINTSCREEN
		KeyboardInputEvent.KEY_INSERT,		// VK_INSERT
		KeyboardInputEvent.KEY_NONE,		// VK_HELP
		KeyboardInputEvent.KEY_RMETA,		// VK_META
		KeyboardInputEvent.KEY_NONE,		// (158)
		KeyboardInputEvent.KEY_NONE,		// (159)
		KeyboardInputEvent.KEY_NONE,		// VK_GREATER
		KeyboardInputEvent.KEY_NONE,		// VK_BRACELEFT
		KeyboardInputEvent.KEY_NONE,		// VK_BRACERIGHT
		KeyboardInputEvent.KEY_NONE,		// (163)
		KeyboardInputEvent.KEY_NONE,		// (164)
		KeyboardInputEvent.KEY_NONE,		// (165)
		KeyboardInputEvent.KEY_NONE,		// (166)
		KeyboardInputEvent.KEY_NONE,		// (167)
		KeyboardInputEvent.KEY_NONE,		// (168)
		KeyboardInputEvent.KEY_NONE,		// (169)
		KeyboardInputEvent.KEY_NONE,		// (170)
		KeyboardInputEvent.KEY_NONE,		// (171)
		KeyboardInputEvent.KEY_NONE,		// (172)
		KeyboardInputEvent.KEY_NONE,		// (173)
		KeyboardInputEvent.KEY_NONE,		// (174)
		KeyboardInputEvent.KEY_NONE,		// (175)
		KeyboardInputEvent.KEY_NONE,		// (176)
		KeyboardInputEvent.KEY_NONE,		// (177)
		KeyboardInputEvent.KEY_NONE,		// (178)
		KeyboardInputEvent.KEY_NONE,		// (179)
		KeyboardInputEvent.KEY_NONE,		// (180)
		KeyboardInputEvent.KEY_NONE,		// (181)
		KeyboardInputEvent.KEY_NONE,		// (182)
		KeyboardInputEvent.KEY_NONE,		// (183)
		KeyboardInputEvent.KEY_NONE,		// (184)
		KeyboardInputEvent.KEY_NONE,		// (185)
		KeyboardInputEvent.KEY_NONE,		// (186)
		KeyboardInputEvent.KEY_NONE,		// (187)
		KeyboardInputEvent.KEY_NONE,		// (188)
		KeyboardInputEvent.KEY_NONE,		// (189)
		KeyboardInputEvent.KEY_NONE,		// (190)
		KeyboardInputEvent.KEY_NONE,		// (191)
		KeyboardInputEvent.KEY_NONE,		// VK_BACK_QUOTE
		KeyboardInputEvent.KEY_NONE,		// (193)
		KeyboardInputEvent.KEY_NONE,		// (194)
		KeyboardInputEvent.KEY_NONE,		// (195)
		KeyboardInputEvent.KEY_NONE,		// (196)
		KeyboardInputEvent.KEY_NONE,		// (197)
		KeyboardInputEvent.KEY_NONE,		// (198)
		KeyboardInputEvent.KEY_NONE,		// (199)
		KeyboardInputEvent.KEY_NONE,		// (200)
		KeyboardInputEvent.KEY_NONE,		// (201)
		KeyboardInputEvent.KEY_NONE,		// (202)
		KeyboardInputEvent.KEY_NONE,		// (203)
		KeyboardInputEvent.KEY_NONE,		// (204)
		KeyboardInputEvent.KEY_NONE,		// (205)
		KeyboardInputEvent.KEY_NONE,		// (206)
		KeyboardInputEvent.KEY_NONE,		// (207)
		KeyboardInputEvent.KEY_NONE,		// (208)
		KeyboardInputEvent.KEY_NONE,		// (209)
		KeyboardInputEvent.KEY_NONE,		// (210)
		KeyboardInputEvent.KEY_NONE,		// (211)
		KeyboardInputEvent.KEY_NONE,		// (212)
		KeyboardInputEvent.KEY_NONE,		// (213)
		KeyboardInputEvent.KEY_NONE,		// (214)
		KeyboardInputEvent.KEY_NONE,		// (215)
		KeyboardInputEvent.KEY_NONE,		// (216)
		KeyboardInputEvent.KEY_NONE,		// (217)
		KeyboardInputEvent.KEY_NONE,		// (218)
		KeyboardInputEvent.KEY_NONE,		// (219)
		KeyboardInputEvent.KEY_NONE,		// (220)
		KeyboardInputEvent.KEY_NONE,		// (221)
		KeyboardInputEvent.KEY_NONE,		// VK_QUOTE
		KeyboardInputEvent.KEY_NONE,		// (223)
		KeyboardInputEvent.KEY_UP,		// VK_KP_UP
		KeyboardInputEvent.KEY_DOWN,		// VK_KP_DOWN
		KeyboardInputEvent.KEY_LEFT,		// VK_KP_LEFT
		KeyboardInputEvent.KEY_RIGHT,		// VK_KP_RIGHT
		KeyboardInputEvent.KEY_NONE,		// (228)
		KeyboardInputEvent.KEY_NONE,		// (229)
		KeyboardInputEvent.KEY_NONE,		// (230)
		KeyboardInputEvent.KEY_NONE,		// (231)
		KeyboardInputEvent.KEY_NONE,		// (232)
		KeyboardInputEvent.KEY_NONE,		// (233)
		KeyboardInputEvent.KEY_NONE,		// (234)
		KeyboardInputEvent.KEY_NONE,		// (235)
		KeyboardInputEvent.KEY_NONE,		// (236)
		KeyboardInputEvent.KEY_NONE,		// (237)
		KeyboardInputEvent.KEY_NONE,		// (238)
		KeyboardInputEvent.KEY_NONE,		// (239)
		KeyboardInputEvent.KEY_NONE,		// VK_ALPHANUMERIC
		KeyboardInputEvent.KEY_NONE,		// VK_KATAKANA
		KeyboardInputEvent.KEY_NONE,		// VK_HIRAGANA
		KeyboardInputEvent.KEY_NONE,		// VK_FULL_WIDTH
		KeyboardInputEvent.KEY_NONE,		// VK_HALF_WIDTH
		KeyboardInputEvent.KEY_NONE,		// VK_ROMAN_CHARACTERS
		KeyboardInputEvent.KEY_NONE,		// (246)
		KeyboardInputEvent.KEY_NONE,		// (247)
		KeyboardInputEvent.KEY_NONE,		// (248)
		KeyboardInputEvent.KEY_NONE,		// (249)
		KeyboardInputEvent.KEY_NONE,		// (250)
		KeyboardInputEvent.KEY_NONE,		// (251)
		KeyboardInputEvent.KEY_NONE,		// (252)
		KeyboardInputEvent.KEY_NONE,		// (253)
		KeyboardInputEvent.KEY_NONE,		// (254)
		KeyboardInputEvent.KEY_NONE,		// (255)
		KeyboardInputEvent.KEY_NONE,		// VK_ALL_CANDIDATES
		KeyboardInputEvent.KEY_NONE,		// VK_PREVIOUS_CANDIDATE
		KeyboardInputEvent.KEY_NONE,		// VK_CODE_INPUT
		KeyboardInputEvent.KEY_NONE,		// VK_JAPANESE_KATAKANA
		KeyboardInputEvent.KEY_NONE,		// VK_JAPANESE_HIRAGANA
		KeyboardInputEvent.KEY_NONE,		// VK_JAPANESE_ROMAN
		KeyboardInputEvent.KEY_KANA,		// VK_KANA_LOCK
		KeyboardInputEvent.KEY_NONE,		// VK_INPUT_METHOD_ON_OFF
		KeyboardInputEvent.KEY_NONE,		// (264)
		KeyboardInputEvent.KEY_NONE,		// (265)
		KeyboardInputEvent.KEY_NONE,		// (266)
		KeyboardInputEvent.KEY_NONE,		// (267)
		KeyboardInputEvent.KEY_NONE,		// (268)
		KeyboardInputEvent.KEY_NONE,		// (269)
		KeyboardInputEvent.KEY_NONE,		// (270)
		KeyboardInputEvent.KEY_NONE,		// (271)
		KeyboardInputEvent.KEY_NONE,		// (272)
		KeyboardInputEvent.KEY_NONE,		// (273)
		KeyboardInputEvent.KEY_NONE,		// (274)
		KeyboardInputEvent.KEY_NONE,		// (275)
		KeyboardInputEvent.KEY_NONE,		// (276)
		KeyboardInputEvent.KEY_NONE,		// (277)
		KeyboardInputEvent.KEY_NONE,		// (278)
		KeyboardInputEvent.KEY_NONE,		// (279)
		KeyboardInputEvent.KEY_NONE,		// (280)
		KeyboardInputEvent.KEY_NONE,		// (281)
		KeyboardInputEvent.KEY_NONE,		// (282)
		KeyboardInputEvent.KEY_NONE,		// (283)
		KeyboardInputEvent.KEY_NONE,		// (284)
		KeyboardInputEvent.KEY_NONE,		// (285)
		KeyboardInputEvent.KEY_NONE,		// (286)
		KeyboardInputEvent.KEY_NONE,		// (287)
		KeyboardInputEvent.KEY_NONE,		// (288)
		KeyboardInputEvent.KEY_NONE,		// (289)
		KeyboardInputEvent.KEY_NONE,		// (290)
		KeyboardInputEvent.KEY_NONE,		// (291)
		KeyboardInputEvent.KEY_NONE,		// (292)
		KeyboardInputEvent.KEY_NONE,		// (293)
		KeyboardInputEvent.KEY_NONE,		// (294)
		KeyboardInputEvent.KEY_NONE,		// (295)
		KeyboardInputEvent.KEY_NONE,		// (296)
		KeyboardInputEvent.KEY_NONE,		// (297)
		KeyboardInputEvent.KEY_NONE,		// (298)
		KeyboardInputEvent.KEY_NONE,		// (299)
		KeyboardInputEvent.KEY_NONE,		// (300)
		KeyboardInputEvent.KEY_NONE,		// (301)
		KeyboardInputEvent.KEY_NONE,		// (302)
		KeyboardInputEvent.KEY_NONE,		// (303)
		KeyboardInputEvent.KEY_NONE,		// (304)
		KeyboardInputEvent.KEY_NONE,		// (305)
		KeyboardInputEvent.KEY_NONE,		// (306)
		KeyboardInputEvent.KEY_NONE,		// (307)
		KeyboardInputEvent.KEY_NONE,		// (308)
		KeyboardInputEvent.KEY_NONE,		// (309)
		KeyboardInputEvent.KEY_NONE,		// (310)
		KeyboardInputEvent.KEY_NONE,		// (311)
		KeyboardInputEvent.KEY_NONE,		// (312)
		KeyboardInputEvent.KEY_NONE,		// (313)
		KeyboardInputEvent.KEY_NONE,		// (314)
		KeyboardInputEvent.KEY_NONE,		// (315)
		KeyboardInputEvent.KEY_NONE,		// (316)
		KeyboardInputEvent.KEY_NONE,		// (317)
		KeyboardInputEvent.KEY_NONE,		// (318)
		KeyboardInputEvent.KEY_NONE,		// (319)
		KeyboardInputEvent.KEY_NONE,		// (320)
		KeyboardInputEvent.KEY_NONE,		// (321)
		KeyboardInputEvent.KEY_NONE,		// (322)
		KeyboardInputEvent.KEY_NONE,		// (323)
		KeyboardInputEvent.KEY_NONE,		// (324)
		KeyboardInputEvent.KEY_NONE,		// (325)
		KeyboardInputEvent.KEY_NONE,		// (326)
		KeyboardInputEvent.KEY_NONE,		// (327)
		KeyboardInputEvent.KEY_NONE,		// (328)
		KeyboardInputEvent.KEY_NONE,		// (329)
		KeyboardInputEvent.KEY_NONE,		// (330)
		KeyboardInputEvent.KEY_NONE,		// (331)
		KeyboardInputEvent.KEY_NONE,		// (332)
		KeyboardInputEvent.KEY_NONE,		// (333)
		KeyboardInputEvent.KEY_NONE,		// (334)
		KeyboardInputEvent.KEY_NONE,		// (335)
		KeyboardInputEvent.KEY_NONE,		// (336)
		KeyboardInputEvent.KEY_NONE,		// (337)
		KeyboardInputEvent.KEY_NONE,		// (338)
		KeyboardInputEvent.KEY_NONE,		// (339)
		KeyboardInputEvent.KEY_NONE,		// (340)
		KeyboardInputEvent.KEY_NONE,		// (341)
		KeyboardInputEvent.KEY_NONE,		// (342)
		KeyboardInputEvent.KEY_NONE,		// (343)
		KeyboardInputEvent.KEY_NONE,		// (344)
		KeyboardInputEvent.KEY_NONE,		// (345)
		KeyboardInputEvent.KEY_NONE,		// (346)
		KeyboardInputEvent.KEY_NONE,		// (347)
		KeyboardInputEvent.KEY_NONE,		// (348)
		KeyboardInputEvent.KEY_NONE,		// (349)
		KeyboardInputEvent.KEY_NONE,		// (350)
		KeyboardInputEvent.KEY_NONE,		// (351)
		KeyboardInputEvent.KEY_NONE,		// (352)
		KeyboardInputEvent.KEY_NONE,		// (353)
		KeyboardInputEvent.KEY_NONE,		// (354)
		KeyboardInputEvent.KEY_NONE,		// (355)
		KeyboardInputEvent.KEY_NONE,		// (356)
		KeyboardInputEvent.KEY_NONE,		// (357)
		KeyboardInputEvent.KEY_NONE,		// (358)
		KeyboardInputEvent.KEY_NONE,		// (359)
		KeyboardInputEvent.KEY_NONE,		// (360)
		KeyboardInputEvent.KEY_NONE,		// (361)
		KeyboardInputEvent.KEY_NONE,		// (362)
		KeyboardInputEvent.KEY_NONE,		// (363)
		KeyboardInputEvent.KEY_NONE,		// (364)
		KeyboardInputEvent.KEY_NONE,		// (365)
		KeyboardInputEvent.KEY_NONE,		// (366)
		KeyboardInputEvent.KEY_NONE,		// (367)
		KeyboardInputEvent.KEY_NONE,		// (368)
		KeyboardInputEvent.KEY_NONE,		// (369)
		KeyboardInputEvent.KEY_NONE,		// (370)
		KeyboardInputEvent.KEY_NONE,		// (371)
		KeyboardInputEvent.KEY_NONE,		// (372)
		KeyboardInputEvent.KEY_NONE,		// (373)
		KeyboardInputEvent.KEY_NONE,		// (374)
		KeyboardInputEvent.KEY_NONE,		// (375)
		KeyboardInputEvent.KEY_NONE,		// (376)
		KeyboardInputEvent.KEY_NONE,		// (377)
		KeyboardInputEvent.KEY_NONE,		// (378)
		KeyboardInputEvent.KEY_NONE,		// (379)
		KeyboardInputEvent.KEY_NONE,		// (380)
		KeyboardInputEvent.KEY_NONE,		// (381)
		KeyboardInputEvent.KEY_NONE,		// (382)
		KeyboardInputEvent.KEY_NONE,		// (383)
		KeyboardInputEvent.KEY_NONE,		// (384)
		KeyboardInputEvent.KEY_NONE,		// (385)
		KeyboardInputEvent.KEY_NONE,		// (386)
		KeyboardInputEvent.KEY_NONE,		// (387)
		KeyboardInputEvent.KEY_NONE,		// (388)
		KeyboardInputEvent.KEY_NONE,		// (389)
		KeyboardInputEvent.KEY_NONE,		// (390)
		KeyboardInputEvent.KEY_NONE,		// (391)
		KeyboardInputEvent.KEY_NONE,		// (392)
		KeyboardInputEvent.KEY_NONE,		// (393)
		KeyboardInputEvent.KEY_NONE,		// (394)
		KeyboardInputEvent.KEY_NONE,		// (395)
		KeyboardInputEvent.KEY_NONE,		// (396)
		KeyboardInputEvent.KEY_NONE,		// (397)
		KeyboardInputEvent.KEY_NONE,		// (398)
		KeyboardInputEvent.KEY_NONE,		// (399)
		KeyboardInputEvent.KEY_NONE,		// (400)
		KeyboardInputEvent.KEY_NONE,		// (401)
		KeyboardInputEvent.KEY_NONE,		// (402)
		KeyboardInputEvent.KEY_NONE,		// (403)
		KeyboardInputEvent.KEY_NONE,		// (404)
		KeyboardInputEvent.KEY_NONE,		// (405)
		KeyboardInputEvent.KEY_NONE,		// (406)
		KeyboardInputEvent.KEY_NONE,		// (407)
		KeyboardInputEvent.KEY_NONE,		// (408)
		KeyboardInputEvent.KEY_NONE,		// (409)
		KeyboardInputEvent.KEY_NONE,		// (410)
		KeyboardInputEvent.KEY_NONE,		// (411)
		KeyboardInputEvent.KEY_NONE,		// (412)
		KeyboardInputEvent.KEY_NONE,		// (413)
		KeyboardInputEvent.KEY_NONE,		// (414)
		KeyboardInputEvent.KEY_NONE,		// (415)
		KeyboardInputEvent.KEY_NONE,		// (416)
		KeyboardInputEvent.KEY_NONE,		// (417)
		KeyboardInputEvent.KEY_NONE,		// (418)
		KeyboardInputEvent.KEY_NONE,		// (419)
		KeyboardInputEvent.KEY_NONE,		// (420)
		KeyboardInputEvent.KEY_NONE,		// (421)
		KeyboardInputEvent.KEY_NONE,		// (422)
		KeyboardInputEvent.KEY_NONE,		// (423)
		KeyboardInputEvent.KEY_NONE,		// (424)
		KeyboardInputEvent.KEY_NONE,		// (425)
		KeyboardInputEvent.KEY_NONE,		// (426)
		KeyboardInputEvent.KEY_NONE,		// (427)
		KeyboardInputEvent.KEY_NONE,		// (428)
		KeyboardInputEvent.KEY_NONE,		// (429)
		KeyboardInputEvent.KEY_NONE,		// (430)
		KeyboardInputEvent.KEY_NONE,		// (431)
		KeyboardInputEvent.KEY_NONE,		// (432)
		KeyboardInputEvent.KEY_NONE,		// (433)
		KeyboardInputEvent.KEY_NONE,		// (434)
		KeyboardInputEvent.KEY_NONE,		// (435)
		KeyboardInputEvent.KEY_NONE,		// (436)
		KeyboardInputEvent.KEY_NONE,		// (437)
		KeyboardInputEvent.KEY_NONE,		// (438)
		KeyboardInputEvent.KEY_NONE,		// (439)
		KeyboardInputEvent.KEY_NONE,		// (440)
		KeyboardInputEvent.KEY_NONE,		// (441)
		KeyboardInputEvent.KEY_NONE,		// (442)
		KeyboardInputEvent.KEY_NONE,		// (443)
		KeyboardInputEvent.KEY_NONE,		// (444)
		KeyboardInputEvent.KEY_NONE,		// (445)
		KeyboardInputEvent.KEY_NONE,		// (446)
		KeyboardInputEvent.KEY_NONE,		// (447)
		KeyboardInputEvent.KEY_NONE,		// (448)
		KeyboardInputEvent.KEY_NONE,		// (449)
		KeyboardInputEvent.KEY_NONE,		// (450)
		KeyboardInputEvent.KEY_NONE,		// (451)
		KeyboardInputEvent.KEY_NONE,		// (452)
		KeyboardInputEvent.KEY_NONE,		// (453)
		KeyboardInputEvent.KEY_NONE,		// (454)
		KeyboardInputEvent.KEY_NONE,		// (455)
		KeyboardInputEvent.KEY_NONE,		// (456)
		KeyboardInputEvent.KEY_NONE,		// (457)
		KeyboardInputEvent.KEY_NONE,		// (458)
		KeyboardInputEvent.KEY_NONE,		// (459)
		KeyboardInputEvent.KEY_NONE,		// (460)
		KeyboardInputEvent.KEY_NONE,		// (461)
		KeyboardInputEvent.KEY_NONE,		// (462)
		KeyboardInputEvent.KEY_NONE,		// (463)
		KeyboardInputEvent.KEY_NONE,		// (464)
		KeyboardInputEvent.KEY_NONE,		// (465)
		KeyboardInputEvent.KEY_NONE,		// (466)
		KeyboardInputEvent.KEY_NONE,		// (467)
		KeyboardInputEvent.KEY_NONE,		// (468)
		KeyboardInputEvent.KEY_NONE,		// (469)
		KeyboardInputEvent.KEY_NONE,		// (470)
		KeyboardInputEvent.KEY_NONE,		// (471)
		KeyboardInputEvent.KEY_NONE,		// (472)
		KeyboardInputEvent.KEY_NONE,		// (473)
		KeyboardInputEvent.KEY_NONE,		// (474)
		KeyboardInputEvent.KEY_NONE,		// (475)
		KeyboardInputEvent.KEY_NONE,		// (476)
		KeyboardInputEvent.KEY_NONE,		// (477)
		KeyboardInputEvent.KEY_NONE,		// (478)
		KeyboardInputEvent.KEY_NONE,		// (479)
		KeyboardInputEvent.KEY_NONE,		// (480)
		KeyboardInputEvent.KEY_NONE,		// (481)
		KeyboardInputEvent.KEY_NONE,		// (482)
		KeyboardInputEvent.KEY_NONE,		// (483)
		KeyboardInputEvent.KEY_NONE,		// (484)
		KeyboardInputEvent.KEY_NONE,		// (485)
		KeyboardInputEvent.KEY_NONE,		// (486)
		KeyboardInputEvent.KEY_NONE,		// (487)
		KeyboardInputEvent.KEY_NONE,		// (488)
		KeyboardInputEvent.KEY_NONE,		// (489)
		KeyboardInputEvent.KEY_NONE,		// (490)
		KeyboardInputEvent.KEY_NONE,		// (491)
		KeyboardInputEvent.KEY_NONE,		// (492)
		KeyboardInputEvent.KEY_NONE,		// (493)
		KeyboardInputEvent.KEY_NONE,		// (494)
		KeyboardInputEvent.KEY_NONE,		// (495)
		KeyboardInputEvent.KEY_NONE,		// (496)
		KeyboardInputEvent.KEY_NONE,		// (497)
		KeyboardInputEvent.KEY_NONE,		// (498)
		KeyboardInputEvent.KEY_NONE,		// (499)
		KeyboardInputEvent.KEY_NONE,		// (500)
		KeyboardInputEvent.KEY_NONE,		// (501)
		KeyboardInputEvent.KEY_NONE,		// (502)
		KeyboardInputEvent.KEY_NONE,		// (503)
		KeyboardInputEvent.KEY_NONE,		// (504)
		KeyboardInputEvent.KEY_NONE,		// (505)
		KeyboardInputEvent.KEY_NONE,		// (506)
		KeyboardInputEvent.KEY_NONE,		// (507)
		KeyboardInputEvent.KEY_NONE,		// (508)
		KeyboardInputEvent.KEY_NONE,		// (509)
		KeyboardInputEvent.KEY_NONE,		// (510)
		KeyboardInputEvent.KEY_NONE,		// (511)
		KeyboardInputEvent.KEY_AT,		// VK_AT
		KeyboardInputEvent.KEY_COLON,		// VK_COLON
		KeyboardInputEvent.KEY_CIRCUMFLEX,		// VK_CIRCUMFLEX
		KeyboardInputEvent.KEY_NONE,		// VK_DOLLAR
		KeyboardInputEvent.KEY_NONE,		// VK_EURO_SIGN
		KeyboardInputEvent.KEY_NONE,		// VK_EXCLAMATION_MARK
		KeyboardInputEvent.KEY_NONE,		// VK_INVERTED_EXCLAMATION_MARK
		KeyboardInputEvent.KEY_NONE,		// VK_LEFT_PARENTHESIS
		KeyboardInputEvent.KEY_NONE,		// VK_NUMBER_SIGN
		KeyboardInputEvent.KEY_NONE,		// VK_PLUS
		KeyboardInputEvent.KEY_NONE,		// VK_RIGHT_PARENTHESIS
		KeyboardInputEvent.KEY_UNDERLINE,		// VK_UNDERSCORE
		KeyboardInputEvent.KEY_RMETA,		// VK_WINDOWS
		KeyboardInputEvent.KEY_LMENU,		// VK_CONTEXT_MENU
	};

	protected List<KeyboardInputEvent> keyEventList = Collections.synchronizedList(new ArrayList<KeyboardInputEvent>());
	protected List<MouseEvent> mouseEventList = Collections.synchronizedList(new ArrayList<MouseEvent>());

	public static int nfKeyCode2Nifty(int k) {
		if(k >= 0 && k < KEYTABLE.length) {
			return KEYTABLE[k];
		}
		return KeyboardInputEvent.KEY_NONE;
	}

	public NFInputSystem() {
	}

	public NFInputSystem(NFKeyboard keyboard, NFMouse mouse) {
		if(keyboard != null) {
			keyboard.addKeyListener(this);
		} else {
			log.warn("keyboard is null");
		}
		if(mouse != null) {
			mouse.addMouseListener(this);
		} else {
			log.warn("mouse is null");
		}
	}

	public void forwardEvents(NiftyInputConsumer inputEventConsumer) {
		synchronized (keyEventList) {
			Iterator<KeyboardInputEvent> it = keyEventList.iterator();
			while(it.hasNext()) {
				inputEventConsumer.processKeyboardEvent(it.next());
				it.remove();
			}
		}

		synchronized (mouseEventList) {
			Iterator<MouseEvent> it = mouseEventList.iterator();
			while(it.hasNext()) {
				MouseEvent e = it.next();
				inputEventConsumer.processMouseEvent(e.mouseX, e.mouseY, e.mouseWheel, e.button, e.buttonDown);
				it.remove();
			}
		}
	}

	public void setMousePosition(int x, int y) {

	}

	public void keyPressed(NFKeyboard keyboard, int key, char c) {
		boolean shiftDown = keyboard.isKeyDown(KeyEvent.VK_SHIFT);
		boolean ctrlDown = keyboard.isKeyDown(KeyEvent.VK_CONTROL);
		KeyboardInputEvent event = new KeyboardInputEvent(nfKeyCode2Nifty(key), c, true, shiftDown, ctrlDown);
		keyEventList.add(event);
	}

	public void keyReleased(NFKeyboard keyboard, int key, char c) {
		boolean shiftDown = keyboard.isKeyDown(KeyEvent.VK_SHIFT);
		boolean ctrlDown = keyboard.isKeyDown(KeyEvent.VK_CONTROL);
		KeyboardInputEvent event = new KeyboardInputEvent(nfKeyCode2Nifty(key), c, false, shiftDown, ctrlDown);
		keyEventList.add(event);
	}

	public void mouseMoved(NFMouse mouse, int oldx, int oldy, int newx, int newy) {
		MouseEvent e = new MouseEvent(newx, newy, 0, -1, false);
		mouseEventList.add(e);
	}

	public void mouseDragged(NFMouse mouse, int oldx, int oldy, int newx, int newy) {
		int button = -1;
		if(mouse.isLeftButtonDown()) button = 0;
		else if(mouse.isRightButtonDown()) button = 1;
		else if(mouse.isMiddleButtonDown()) button = 2;

		MouseEvent e = new MouseEvent(newx, newy, 0, button, true);
		mouseEventList.add(e);
	}

	public void mousePressed(NFMouse mouse, int button, int x, int y) {
		MouseEvent e = new MouseEvent(x, y, 0, button, true);
		mouseEventList.add(e);
	}

	public void mouseReleased(NFMouse mouse, int button, int x, int y) {
		MouseEvent e = new MouseEvent(x, y, 0, button, false);
		mouseEventList.add(e);
	}

	public void mouseClicked(NFMouse mouse, int button, int x, int y, int clickCount) {
	}

	public void mouseWheelMoved(NFMouse mouse, int change) {
		MouseEvent e = new MouseEvent(mouse.getMouseX(), mouse.getMouseY(), change, -1, false);
		mouseEventList.add(e);
	}

	public static class MouseEvent {
		private int mouseX, mouseY;
		private int mouseWheel;
		private int button;
		private boolean buttonDown;

		public MouseEvent(int mouseX, int mouseY, int mouseWheel, int button, boolean buttonDown) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			this.mouseWheel = mouseWheel;
			this.button = button;
			this.buttonDown = buttonDown;
		}

		public int getMouseX() {
			return mouseX;
		}

		public int getMouseY() {
			return mouseY;
		}

		public int getMouseWheel() {
			return mouseWheel;
		}

		public int getButton() {
			return button;
		}

		public boolean isButtonDown() {
			return buttonDown;
		}
	}
}
