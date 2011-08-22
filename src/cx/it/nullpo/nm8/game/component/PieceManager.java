package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PieceManager: Factory of Piece
 */
public class PieceManager implements Serializable {
	private static final long serialVersionUID = 7186849117968185144L;

	/** Piece ID constants */
	public static final int PIECE_NONE = -1,
							PIECE_I = 0,
							PIECE_L = 1,
							PIECE_O = 2,
							PIECE_Z = 3,
							PIECE_T = 4,
							PIECE_J = 5,
							PIECE_S = 6,
							PIECE_I1 = 7,
							PIECE_I2 = 8,
							PIECE_I3 = 9,
							PIECE_L3 = 10;

	/** Piece name constants */
	public static final String[] PIECE_NAMES = {"I","L","O","Z","T","J","S","I1","I2","I3","L3"};

	/** Number of standard pieces */
	public static final int PIECE_STANDARD_COUNT = 7;

	/** Number of all pieces */
	public static final int PIECE_COUNT = 11;

	/** Piece data (X-coordinate) */
	public static final int[][][] DEFAULT_PIECE_DATA_X = {
		{{0,1,2,3},{2,2,2,2},{3,2,1,0},{1,1,1,1}},	// I
		{{2,2,1,0},{2,1,1,1},{0,0,1,2},{0,1,1,1}},	// L
		{{0,1,1,0},{1,1,0,0},{1,0,0,1},{0,0,1,1}},	// O
		{{0,1,1,2},{2,2,1,1},{2,1,1,0},{0,0,1,1}},	// Z
		{{1,0,1,2},{2,1,1,1},{1,2,1,0},{0,1,1,1}},	// T
		{{0,0,1,2},{2,1,1,1},{2,2,1,0},{0,1,1,1}},	// J
		{{2,1,1,0},{2,2,1,1},{0,1,1,2},{0,0,1,1}},	// S
		{{0      },{0      },{0      },{0      }},	// I1
		{{0,1    },{1,1    },{1,0    },{0,0    }},	// I2
		{{0,1,2  },{1,1,1  },{2,1,0  },{1,1,1  }},	// I3
		{{1,0,0  },{0,0,1  },{0,1,1  },{1,1,0  }},	// L3
	};

	/** Piece data (Y-coordinate) */
	public static final int[][][] DEFAULT_PIECE_DATA_Y = {
		{{1,1,1,1},{0,1,2,3},{2,2,2,2},{3,2,1,0}},	// I
		{{0,1,1,1},{2,2,1,0},{2,1,1,1},{0,0,1,2}},	// L
		{{0,0,1,1},{0,1,1,0},{1,1,0,0},{1,0,0,1}},	// O
		{{0,0,1,1},{0,1,1,2},{2,2,1,1},{2,1,1,0}},	// Z
		{{0,1,1,1},{1,0,1,2},{2,1,1,1},{1,2,1,0}},	// T
		{{0,1,1,1},{0,0,1,2},{2,1,1,1},{2,2,1,0}},	// J
		{{0,0,1,1},{2,1,1,0},{2,2,1,1},{0,1,1,2}},	// S
		{{0      },{0      },{0      },{0      }},	// I1
		{{0,0    },{0,1    },{1,1    },{1,0    }},	// I2
		{{1,1,1  },{0,1,2  },{1,1,1  },{2,1,0  }},	// I3
		{{1,1,0  },{1,0,0  },{0,0,1  },{0,1,1  }},	// L3
	};

	/** All-spins data A (X-coordinate) */
	public static final int[][][] SPINBONUSDATA_HIGH_X = {
		{{1,2,2,1},{1,3,1,3},{1,2,2,1},{0,2,0,2}},	// I
		{{1,0    },{2,2    },{1,2    },{0,0    }},	// L
		{{       },{       },{       },{       }},	// O
		{{2,0    },{2,1    },{0,2    },{0,1    }},	// Z
		{{0,2    },{2,2    },{0,2    },{0,0    }},	// T
		{{1,2    },{2,2    },{1,0    },{0,0    }},	// J
		{{0,2    },{1,2    },{2,0    },{1,0    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** All-spins data A (Y-coordinate) */
	public static final int[][][] SPINBONUSDATA_HIGH_Y = {
		{{0,2,0,2},{1,2,2,1},{1,3,1,3},{1,2,2,1}},	// I
		{{0,0    },{1,0    },{2,2    },{1,2    }},	// L
		{{       },{       },{       },{       }},	// O
		{{0,1    },{2,0    },{2,1    },{0,2    }},	// Z
		{{0,0    },{0,2    },{2,2    },{0,2    }},	// T
		{{0,0    },{1,2    },{2,2    },{1,0    }},	// J
		{{0,1    },{2,0    },{2,1    },{0,2    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** All-spins data B (X-coordinate) */
	public static final int[][][] SPINBONUSDATA_LOW_X = {
		{{-1,4,-1,4},{2,2,2,2},{-1,4,-1,4},{1,1,1,1}},	// I
		{{2,0    },{0,0    },{0,2    },{2,2    }},	// L
		{{       },{       },{       },{       }},	// O
		{{-1,3   },{2,1    },{3,-1   },{0,1    }},	// Z
		{{0,2    },{0,0    },{0,2    },{2,2    }},	// T
		{{0,2    },{0,0    },{2,0    },{2,2    }},	// J
		{{3,-1   },{1,2    },{-1,3   },{1,0    }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** All-spins data B (Y-coordinate) */
	public static final int[][][] SPINBONUSDATA_LOW_Y = {
		{{1,1,1,1},{-1,4,-1,4},{2,2,2,2},{-1,4,-1,4}},	// I
		{{2,2    },{2,0    },{0,0    },{0,3    }},	// L
		{{       },{       },{       },{       }},	// O
		{{0,1    },{-1,3   },{2,1    },{3,-1   }},	// Z
		{{2,2    },{0,2    },{0,0    },{0,2    }},	// T
		{{2,2    },{0,2    },{0,0    },{2,0    }},	// J
		{{0,1    },{-1,3   },{2,1    },{3,-1   }},	// S
		{{       },{       },{       },{       }},	// I1
		{{       },{       },{       },{       }},	// I2
		{{       },{       },{       },{       }},	// I3
		{{       },{       },{       },{       }},	// L3
	};

	/** Piece list */
	public List<Piece> listPiece = new ArrayList<Piece>();

	/**
	 * Get piece name by ID
	 * @param id Piece ID
	 * @return Piece name (Returns '?' if ID is invalid)
	 */
	public static String getPieceName(int id) {
		if((id >= 0) && (id < PIECE_NAMES.length)) {
			return PIECE_NAMES[id];
		}
		return "?";
	}

	/**
	 * Constructor
	 */
	public PieceManager() {
		init();
	}

	/**
	 * Copy Constructor
	 * @param m Copy Source
	 */
	public PieceManager(PieceManager m) {
		copy(m);
	}

	/**
	 * Init
	 */
	public void init() {
		// Create default pieces
		listPiece.clear();
		for(int i = 0; i < PIECE_COUNT; i++) {
			Piece p = new Piece(i, PIECE_NAMES[i], DEFAULT_PIECE_DATA_X[i], DEFAULT_PIECE_DATA_Y[i],
								SPINBONUSDATA_HIGH_X[i], SPINBONUSDATA_HIGH_Y[i], SPINBONUSDATA_LOW_X[i], SPINBONUSDATA_LOW_Y[i]);
			listPiece.add(p);
		}
	}

	/**
	 * Copy from other PieceManager
	 * @param m Copy Source
	 */
	public void copy(PieceManager m) {
		listPiece.clear();

		for(int i = 0; i < m.listPiece.size(); i++) {
			listPiece.add(new Piece(m.listPiece.get(i)));
		}
	}

	/**
	 * Create a new instance of Piece by ID
	 * @param id Piece ID
	 * @return Piece (null if not found)
	 */
	public Piece newPiece(int id) {
		Iterator<Piece> it = listPiece.iterator();
		while(it.hasNext()) {
			Piece p = it.next();
			if(p.id == id) {
				return new Piece(p);
			}
		}
		return null;
	}

	/**
	 * Create a new instance of Piece by Name
	 * @param name Piece Name
	 * @return Piece (null if not found)
	 */
	public Piece newPiece(String name) {
		Iterator<Piece> it = listPiece.iterator();
		while(it.hasNext()) {
			Piece p = it.next();
			if(p.name.equals(name)) {
				return new Piece(p);
			}
		}
		return null;
	}
}
