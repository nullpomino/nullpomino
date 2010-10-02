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
package mu.nu.nullpo.game.subsystem.mode;

import java.util.LinkedList;
import java.util.Random;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.slick.GameKey;
import mu.nu.nullpo.util.CustomProperties;

/**
 * TOOL-VS MAP EDIT
 */
public class ToolVSMapEditMode extends DummyMode {
	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Map dataの入ったProperty file */
	private CustomProperties propMap;

	/** Current Mapファイルに入っている全field data */
	private LinkedList<Field> listFields;

	/** Current MapセットID */
	private int nowMapSetID;

	/** Current MapID */
	private int nowMapID;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "TOOL-VS MAP EDIT";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;
		propMap = null;
		listFields = new LinkedList<Field>();
		nowMapSetID = 0;
		nowMapID = 0;
	}

	/**
	 * Map読み込み
	 * @param field field
	 * @param prop Property file to read from
	 * @param preset 任意のID
	 */
	private void loadMap(Field field, CustomProperties prop, int id) {
		field.reset();
		//field.readProperty(prop, id);
		field.stringToField(prop.getProperty("map." + id, ""));
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
	}

	/**
	 * Map保存
	 * @param field field
	 * @param prop Property file to save to
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * 全Map読み込み
	 * @param setID MapセットID
	 */
	private void loadAllMaps(int setID) {
		propMap = receiver.loadProperties("config/map/vsbattle/" + setID + ".map");
		if(propMap == null) propMap = new CustomProperties();

		listFields.clear();

		int maxMap = propMap.getProperty("map.maxMapNumber", 0);
		for(int i = 0; i < maxMap; i++) {
			Field fld = new Field();
			loadMap(fld, propMap, i);
			listFields.add(fld);
		}
	}

	/**
	 * 全Map保存
	 * @param setID MapセットID
	 */
	private void saveAllMaps(int setID) {
		propMap = new CustomProperties();

		int maxMap = listFields.size();
		propMap.setProperty("map.maxMapNumber", maxMap);

		for(int i = 0; i < maxMap; i++) {
			saveMap(listFields.get(i), propMap, i);
		}

		receiver.saveProperties("config/map/vsbattle/" + setID + ".map", propMap);
	}

	private void grayToRandomColor(Field field) {
		Random rand = new Random();

		for(int i = (field.getHiddenHeight() * -1); i < field.getHeight(); i++) {
			for(int j = 0; j < field.getWidth(); j++) {
				if(field.getBlockColor(j, i) == Block.BLOCK_COLOR_GRAY) {
					int color = -1;
					do {
						color = rand.nextInt(Block.BLOCK_COLOR_COUNT - 2) + 2;
					} while ((color == field.getBlockColor(j - 1, i)) || (color == field.getBlockColor(j + 1, i)) ||
							 (color == field.getBlockColor(j, i - 1)) || (color == field.getBlockColor(j, i - 1)));
					field.setBlockColor(j, i, color);
				}
			}
		}
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
		engine.createFieldIfNeeded();
		loadAllMaps(nowMapSetID);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Configuration changes
		int change = updateCursor(engine, 7);

		if(change != 0) {
			engine.playSE("change");

			switch(engine.statc[2]) {
			case 0:
			case 1:
			case 2:
				break;
			case 3:
			case 4:
			case 5:
				nowMapID += change;
				if(nowMapID < 0) nowMapID = listFields.size();
				if(nowMapID > listFields.size()) nowMapID = 0;
				break;
			case 6:
			case 7:
				nowMapSetID += change;
				if(nowMapSetID < 0) nowMapSetID = 99;
				if(nowMapSetID > 99) nowMapSetID = 0;
				break;
			}
		}

		// 決定
		if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
			engine.playSE("decide");

			if(engine.statc[2] == 0) {
				// EDIT
				engine.enterFieldEdit();
			} else if(engine.statc[2] == 1) {
				// GRAY->?
				grayToRandomColor(engine.field);
			} else if(engine.statc[2] == 2) {
				// CLEAR
				engine.field.reset();
			} else if(engine.statc[2] == 3) {
				// SAVE
				if((nowMapID >= 0) && (nowMapID < listFields.size())) {
					listFields.get(nowMapID).copy(engine.field);
				} else {
					listFields.add(new Field(engine.field));
				}
			} else if(engine.statc[2] == 4) {
				// LOAD
				if((nowMapID >= 0) && (nowMapID < listFields.size())) {
					engine.field.copy(listFields.get(nowMapID));
					engine.field.setAllSkin(engine.getSkin());
				} else {
					engine.field.reset();
				}
			} else if(engine.statc[2] == 5) {
				// DELETE
				if((nowMapID >= 0) && (nowMapID < listFields.size())) {
					listFields.remove(nowMapID);
					if(nowMapID >= listFields.size()) nowMapID = listFields.size();
				}
			} else if(engine.statc[2] == 6) {
				// WRITE
				saveAllMaps(nowMapSetID);
			} else if(engine.statc[2] == 7) {
				// READ
				loadAllMaps(nowMapSetID);
				nowMapID = 0;
				engine.field.reset();
			}
		}

		// 終了
		if(engine.ctrl.isPress(Controller.BUTTON_D) && engine.ctrl.isPress(Controller.BUTTON_E) && (engine.statc[3] >= 5)) {
			engine.quitflag = true;
		}

		engine.statc[3]++;
		return true;
	}

	/*
	 * 設定画面の描画
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 1, "FIELD EDIT", EventReceiver.COLOR_DARKBLUE);
		if((engine.statc[2] >= 0) && (engine.statc[2] <= 2)) {
			receiver.drawMenuFont(engine, playerID, 0, 2 + engine.statc[2], "b", EventReceiver.COLOR_RED);
		}
		receiver.drawMenuFont(engine, playerID, 1, 2, "[EDIT]", (engine.statc[2] == 0));
		receiver.drawMenuFont(engine, playerID, 1, 3, "[GRAY->?]", (engine.statc[2] == 1));
		receiver.drawMenuFont(engine, playerID, 1, 4, "[CLEAR]", (engine.statc[2] == 2));

		receiver.drawMenuFont(engine, playerID, 0, 6, "MAP DATA", EventReceiver.COLOR_DARKBLUE);
		if(listFields.size() > 0) {
			receiver.drawMenuFont(engine, playerID, 0, 7, nowMapID + "/" + (listFields.size() - 1), (engine.statc[2] >= 3) && (engine.statc[2] <= 5));
		} else {
			receiver.drawMenuFont(engine, playerID, 0, 7, "NO MAPS", (engine.statc[2] >= 3) && (engine.statc[2] <= 5));
		}
		if((engine.statc[2] >= 3) && (engine.statc[2] <= 5)) {
			receiver.drawMenuFont(engine, playerID, 0, 8 + engine.statc[2] - 3, "b", EventReceiver.COLOR_RED);
		}
		receiver.drawMenuFont(engine, playerID, 1, 8, "[SAVE]", (engine.statc[2] == 3));
		receiver.drawMenuFont(engine, playerID, 1, 9, "[LOAD]", (engine.statc[2] == 4));
		receiver.drawMenuFont(engine, playerID, 1, 10, "[DELETE]", (engine.statc[2] == 5));

		receiver.drawMenuFont(engine, playerID, 0, 12, "MAP FILE", EventReceiver.COLOR_DARKBLUE);
		receiver.drawMenuFont(engine, playerID, 0, 13, nowMapSetID + "/99", (engine.statc[2] >= 6) && (engine.statc[2] <= 7));
		if((engine.statc[2] >= 6) && (engine.statc[2] <= 7)) {
			receiver.drawMenuFont(engine, playerID, 0, 14 + engine.statc[2] - 6, "b", EventReceiver.COLOR_RED);
		}
		receiver.drawMenuFont(engine, playerID, 1, 14, "[WRITE]", (engine.statc[2] == 6));
		receiver.drawMenuFont(engine, playerID, 1, 15, "[READ]", (engine.statc[2] == 7));

		receiver.drawMenuFont(engine, playerID, 0, 19, "EXIT-> D+E", EventReceiver.COLOR_ORANGE);
	}

	/*
	 * fieldエディット画面
	 */
	@Override
	public void renderFieldEdit(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 2, "X POS", EventReceiver.COLOR_BLUE);
		receiver.drawScoreFont(engine, playerID, 0, 3, "" + engine.fldeditX);
		receiver.drawScoreFont(engine, playerID, 0, 4, "Y POS", EventReceiver.COLOR_BLUE);
		receiver.drawScoreFont(engine, playerID, 0, 5, "" + engine.fldeditY);
	}
}
