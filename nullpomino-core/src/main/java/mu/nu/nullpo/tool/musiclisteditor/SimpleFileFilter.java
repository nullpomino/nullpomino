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
package mu.nu.nullpo.tool.musiclisteditor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * シンプルなファイルフィルタ
 */
public class SimpleFileFilter extends FileFilter {
	/** 拡張子 */
	protected String extension;

	/** 表示名 */
	protected String description;

	/**
	 * Constructor
	 */
	public SimpleFileFilter() {
		super();
		this.extension = "";
		this.description = "";
	}

	/**
	 * Constructor
	 * @param extension 拡張子
	 */
	public SimpleFileFilter(String extension) {
		super();
		this.extension = extension;
		this.description = "";
	}

	/**
	 * Constructor
	 * @param extension 拡張子
	 * @param description 表示名
	 */
	public SimpleFileFilter(String extension, String description) {
		super();
		this.extension = extension;
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()) return true;
		if(f.getName().endsWith(extension)) return true;
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension セットする extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
}
