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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * サウンドエンジン
 * <a href="http://javagame.skr.jp/index.php?%A5%B5%A5%A6%A5%F3%A5%C9%A5%A8%A5%F3%A5%B8%A5%F3">転載元</a>
 */
public class WaveEngine implements LineListener {
	/** Log */
	static Logger log = Logger.getLogger(WaveEngine.class);

	/** 登録できるWAVE file のMaximumcount */
	private int maxClips;

	/** WAVE file  data (Name-> data本体) */
	private HashMap<String, Clip> clipMap;

	/** 登録されたWAVE file count */
	private int counter = 0;

	/** 音量 */
	private double volume = 1.0;

	/**
	 * Constructor
	 */
	public WaveEngine() {
		this(128);
	}

	/**
	 * Constructor
	 * @param maxClips 登録できるWAVE file のMaximumcount
	 */
	public WaveEngine(int maxClips) {
		this.maxClips = maxClips;
		clipMap = new HashMap<String, Clip>(maxClips);
	}

	/**
	 * Current 設定音量を取得
	 * @return Current 設定音量 (1.0が default ）
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * 音量を設定
	 * @param vol 新しい設定音量 (1.0が default ）
	 */
	public void setVolume(double vol) {
		volume = vol;

		Set<String> set = clipMap.keySet();
		for(String name: set) {
			try {
				Clip clip = clipMap.get(name);
				FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
				ctrl.setValue((float)Math.log10(volume) * 20);
			} catch (Exception e) {}
		}
	}

	/**
	 * WAVE file を読み込み
	 * @param name 登録名
	 * @param filename Filename
	 */
	public void load(String name, String filename) {
		load(name, ResourceHolderSwing.getURL(filename));
	}

	/**
	 * WAVE file を読み込み
	 * @param name 登録名
	 * @param url URL
	 */
	public void load(String name, URL url) {
		if(counter >= maxClips) {
			log.warn(name + " : No more files can be loaded (Max:" + maxClips + ")");
			return;
		}

		try {
			// オーディオストリームを開く
			AudioInputStream stream = AudioSystem.getAudioInputStream(url);

			// オーディオ形式を取得
			AudioFormat format = stream.getFormat();

			// ULAW/ALAW形式の場合はPCM形式に変更
			if((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
														format.getSampleRate(), format.getSampleSizeInBits() * 2,
														format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
														true);
				stream = AudioSystem.getAudioInputStream(newFormat, stream);
				format = newFormat;
			}

			// Lines情報を取得
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			// 空のクリップを作成
			Clip clip = (Clip) AudioSystem.getLine(info);
			// クリップのイベントを監視
			clip.addLineListener(this);
			// オーディオストリームをクリップとして開く
			clip.open(stream);
			// クリップを登録
			clipMap.put(name, clip);

			// ストリームを閉じる
			stream.close();
		} catch (LineUnavailableException e) {
			log.warn(name + " : Failed to open line", e);
		} catch (UnsupportedAudioFileException e) {
			log.warn(name + " : This is not a wave file", e);
		} catch (IOException e) {
			log.warn(name + " : Load failed", e);
		}
	}

	/**
	 * 再生
	 * @param name 登録名
	 */
	public void play(String name) {
		Clip clip = clipMap.get(name);

		if(clip != null) {
			// 停止
			clip.stop();
			// 再生位置を最初に戻す
			clip.setFramePosition(0);
			// 再生
			clip.start();
		}
	}

	/**
	 * 停止
	 * @param name 登録名
	 */
	public void stop(String name) {
		Clip clip = clipMap.get(name);

		if(clip != null) {
			clip.stop();
		}
	}

	/*
	 * Linesの状態変化
	 */
	public void update(LineEvent event) {
		// ストップか最後まで再生された場合
		if(event.getType() == LineEvent.Type.STOP) {
			Clip clip = (Clip) event.getSource();
			clip.stop();
			clip.setFramePosition(0); // 再生位置を最初に戻す
		}
	}
}
