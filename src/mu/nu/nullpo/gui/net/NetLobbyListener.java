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
package mu.nu.nullpo.gui.net;

import java.io.IOException;

import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;

/**
 * Lobby event interface (also used by netplay modes)
 */
public interface NetLobbyListener {
	/**
	 * Initialization Completed
	 * @param lobby NetLobbyFrame
	 */
	public void netlobbyOnInit(NetLobbyFrame lobby);

	/**
	 * Login completed
	 * @param lobby NetLobbyFrame
	 * @param client NetClient
	 */
	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client);

	/**
	 * When you enter a room
	 * @param lobby NetLobbyFrame
	 * @param client NetClient
	 * @param roomInfo NetRoomInfo
	 */
	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo);

	/**
	 * When you returned to lobby
	 * @param lobby NetLobbyFrame
	 * @param client NetClient
	 */
	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client);

	/**
	 * When disconnected
	 * @param lobby NetLobbyFrame
	 * @param client NetClient
	 * @param ex A Throwable that caused disconnection (null if unknown or normal termination)
	 */
	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex);

	/**
	 * Message received
	 * @param lobby NetLobbyFrame
	 * @param client NetClient
	 * @param message Message (Already sepatated by tabs)
	 * @throws IOException When something bad occurs
	 */
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException;

	/**
	 * When the lobby window is closed
	 * @param lobby NetLobbyFrame
	 */
	public void netlobbyOnExit(NetLobbyFrame lobby);
}
