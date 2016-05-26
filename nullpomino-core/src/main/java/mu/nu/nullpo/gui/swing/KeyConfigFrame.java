/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Key config frame
 */
public class KeyConfigFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Owner window */
	protected NullpoMinoSwing owner;

	/** Player number */
	protected int playerID;

	/** Key event receiver */
	protected KeyConfigKeyEventListener keyEventListener;

	/** Mouse event receiver */
	protected KeyConfigMouseEventListener mouseEventListener;

	/** Key config textbox */
	protected JTextField[] txtfldGameKeys;

	/** Menu key config textbox */
	protected JTextField[] txtfldGameKeysNav;

	/** Key codes */
	protected int[] keyCodes;

	/** Menu key codes */
	protected int[] keyCodesNav;

	/**
	 * Constructor
	 * @param owner Owner window (NullpoMinoSwing)
	 * @throws HeadlessException When GUI cannot be used
	 */
	public KeyConfigFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);

		keyEventListener = new KeyConfigKeyEventListener();
		mouseEventListener = new KeyConfigMouseEventListener();
		keyCodes = new int[GameKeySwing.MAX_BUTTON];
		keyCodesNav = new int[GameKeySwing.MAX_BUTTON];

		initUI();
		pack();
	}

	/**
	 * Load keyboard settings
	 * @param pl Player number
	 */
	public void load(int pl) {
		this.playerID = pl;
		setTitle(NullpoMinoSwing.getUIText("Title_KeyConfig") + " (" + (playerID+1) + "P)");

		for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
			keyCodes[i] = GameKeySwing.gamekey[playerID].keymap[i];
			if(keyCodes[i] == 0) {
				txtfldGameKeys[i].setText("");
			} else {
				txtfldGameKeys[i].setText(KeyEvent.getKeyText(keyCodes[i]));
			}

			keyCodesNav[i] = GameKeySwing.gamekey[playerID].keymapNav[i];
			if(keyCodesNav[i] == 0) {
				txtfldGameKeysNav[i].setText("");
			} else {
				txtfldGameKeysNav[i].setText(KeyEvent.getKeyText(keyCodesNav[i]));
			}
		}
	}

	/**
	 * Save
	 */
	protected void save() {
		for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
			GameKeySwing.gamekey[playerID].keymap[i] = keyCodes[i];
			GameKeySwing.gamekey[playerID].keymapNav[i] = keyCodesNav[i];
		}
		GameKeySwing.gamekey[playerID].saveConfig(NullpoMinoSwing.propConfig);
		NullpoMinoSwing.saveConfig();
	}

	/**
	 * GUI init
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// Hint labels
		JLabel labelHelp1 = new JLabel(NullpoMinoSwing.getUIText("KeyConfig_LabelHelp1"));
		labelHelp1.setAlignmentX(LEFT_ALIGNMENT);
		this.add(labelHelp1);
		JLabel labelHelp2 = new JLabel(NullpoMinoSwing.getUIText("KeyConfig_LabelHelp2"));
		labelHelp2.setAlignmentX(LEFT_ALIGNMENT);
		this.add(labelHelp2);

		// Tab
		JTabbedPane tabKeySetting = new JTabbedPane();
		tabKeySetting.setAlignmentX(LEFT_ALIGNMENT);
		this.add(tabKeySetting);

		// Ingame tab
		JPanel pKeySetting = new JPanel();
		pKeySetting.setLayout(new BoxLayout(pKeySetting, BoxLayout.Y_AXIS));
		pKeySetting.setAlignmentX(LEFT_ALIGNMENT);
		tabKeySetting.addTab(NullpoMinoSwing.getUIText("KeyConfig_Tab_Ingame"), pKeySetting);

		txtfldGameKeys = new JTextField[GameKeySwing.MAX_BUTTON];
		for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
			JPanel psKeyTemp = new JPanel();
			pKeySetting.add(psKeyTemp);
			psKeyTemp.setLayout(new BorderLayout());

			psKeyTemp.add(new JLabel(NullpoMinoSwing.getUIText("KeyConfig_LabelKey" + i)), BorderLayout.WEST);

			txtfldGameKeys[i] = new JTextField(20);
			txtfldGameKeys[i].addKeyListener(keyEventListener);
			txtfldGameKeys[i].addMouseListener(mouseEventListener);
			txtfldGameKeys[i].setFocusTraversalKeysEnabled(false);
			psKeyTemp.add(txtfldGameKeys[i], BorderLayout.EAST);
		}

		// Menu tab
		JPanel pKeySettingNav = new JPanel();
		pKeySettingNav.setLayout(new BoxLayout(pKeySettingNav, BoxLayout.Y_AXIS));
		pKeySettingNav.setAlignmentX(LEFT_ALIGNMENT);
		tabKeySetting.addTab(NullpoMinoSwing.getUIText("KeyConfig_Tab_Menu"), pKeySettingNav);

		txtfldGameKeysNav = new JTextField[GameKeySwing.MAX_BUTTON];
		for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
			JPanel psKeyTemp = new JPanel();
			pKeySettingNav.add(psKeyTemp);
			psKeyTemp.setLayout(new BorderLayout());

			psKeyTemp.add(new JLabel(NullpoMinoSwing.getUIText("KeyConfig_LabelKey" + i)), BorderLayout.WEST);

			txtfldGameKeysNav[i] = new JTextField(20);
			txtfldGameKeysNav[i].addKeyListener(keyEventListener);
			txtfldGameKeysNav[i].addMouseListener(mouseEventListener);
			txtfldGameKeysNav[i].setFocusTraversalKeysEnabled(false);
			psKeyTemp.add(txtfldGameKeysNav[i], BorderLayout.EAST);
		}

		// Reset tab
		JPanel pKeyReset = new JPanel();
		pKeyReset.setLayout(new BoxLayout(pKeyReset, BoxLayout.Y_AXIS));
		pKeyReset.setAlignmentX(LEFT_ALIGNMENT);
		tabKeySetting.addTab(NullpoMinoSwing.getUIText("KeyConfig_Tab_Reset"), pKeyReset);

		for(int i = 0; i < 3; i++) {
			JButton btnReset = new JButton(NullpoMinoSwing.getUIText("KeyConfig_Reset" + i));
			btnReset.addActionListener(this);
			btnReset.setActionCommand("KeyConfig_Reset" + i);
			btnReset.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
			pKeyReset.add(btnReset);
		}

		// Bottom buttons
		JPanel pButtons = new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
		pButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pButtons);

		JButton btnOK = new JButton(NullpoMinoSwing.getUIText("KeyConfig_OK"));
		btnOK.addActionListener(this);
		btnOK.setActionCommand("KeyConfig_OK");
		btnOK.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		pButtons.add(btnOK);

		JButton btnCancel = new JButton(NullpoMinoSwing.getUIText("KeyConfig_Cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("KeyConfig_Cancel");
		btnCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		pButtons.add(btnCancel);
	}

	/*
	 * Called when a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "KeyConfig_OK") {
			save();
			this.setVisible(false);
		}
		else if(e.getActionCommand() == "KeyConfig_Cancel") {
			this.setVisible(false);
		}
		else if(e.getActionCommand().startsWith("KeyConfig_Reset")) {
			String strTemp = e.getActionCommand().replaceFirst("KeyConfig_Reset", "");
			int id = Integer.parseInt(strTemp);

			GameKeySwing.gamekey[playerID].loadDefaultKeymap(id);
			GameKeySwing.gamekey[playerID].saveConfig(NullpoMinoSwing.propConfig);
			NullpoMinoSwing.saveConfig();
			load(playerID);
		}
	}

	/**
	 * KeyAdapter for key config textboxes
	 */
	protected class KeyConfigKeyEventListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			Component c = e.getComponent();
			for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
				if(c == txtfldGameKeys[i]) {
					keyCodes[i] = e.getKeyCode();
					txtfldGameKeys[i].setText(KeyEvent.getKeyText(e.getKeyCode()));
					break;
				} else if(c == txtfldGameKeysNav[i]) {
					keyCodesNav[i] = e.getKeyCode();
					txtfldGameKeysNav[i].setText(KeyEvent.getKeyText(e.getKeyCode()));
					break;
				}
			}
			e.consume();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			e.consume();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			e.consume();
		}
	}

	/**
	 * MouseAdapter for key config textboxes
	 */
	protected class KeyConfigMouseEventListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			popupButton(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			popupButton(e);
		}

		protected void popupButton(MouseEvent e) {
			if(e.isPopupTrigger()) {
				Component c = e.getComponent();
				for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
					if(c == txtfldGameKeys[i]) {
						keyCodes[i] = 0;
						txtfldGameKeys[i].setText("");
					} else if(c == txtfldGameKeysNav[i]) {
						keyCodes[i] = 0;
						txtfldGameKeysNav[i].setText("");
					}
				}
			}
		}
	}
}
