package search_library.sample_problems;

public class RubiksCube {

	public enum Color {
		YELLOW('y'), RED('r'), GREEN('g'), ORANGE('o'), WHITE('w'), BLUE('b'), none(' ');

		private char rep;

		Color(char s) {
			this.rep = s;
		}
	}

	public enum Actions {
		MOVE_RIGHT, MOVE_LEFT, MOVE_UP, MOVE_DOWN, MOVE_CLOCKWISE, MOVE_COUNTER_CLOCKWISE, SPIN_UPPER_LINE_LEFT, SPIN_UPPER_LINE_RIGHT, SPIN_LOWER_LINE_LEFT, SPIN_LOWER_LINE_RIGHT, SPIN_LEFT_COLUMN_DOWN, SPIN_LEFT_COLUMN_UP, SPIN_RIGHT_COLUMN_DOWN, SPIN_RIGHT_COLUMN_UP, SPIN_FRONT_LINE_CW, SPIN_FRONT_LINE_COUNTER_CW, SPIN_REAR_LINE_CW, SPIN_REAR_LINE_COUNTER_CW,
	}

	/**
	 * The reference to these names is the player observing the cube. Therefore,
	 * the FRONT face is the face which you see directly. The REAR face is its
	 * opposite. The LEFT face is the one to the left of the observer, and the
	 * RIGHT is the one to his right. The UP and DOWN are obvious.
	 * 
	 * The squares of the faces are indexed in this way: the rows (i.e. the
	 * first dimension of the array) are the horizontal lines, while the columns
	 * (second dimension) are the vertical lines. Position FRONT[0][0] is the
	 * top left position of the front face. Other faces are indexed similarly,
	 * considering that they are turned to the front face by using only
	 * LEFT/RIGHT and UP/DOWN rotations.
	 * 
	 * If the cube is disassembled, that's how the faces would be positioned,
	 * with their indexes:
	 * 
	 * UP (0,0) (0,1) (0,2) (1,0) (1,1) (1,2) (2,0) (2,1) (2,2) LEFT FRONT RIGHT
	 * (0,0) (0,1) (0,2) (0,0) (0,1) (0,2) (0,0) (0,1) (0,2) (1,0) (1,1) (1,2)
	 * (1,0) (1,1) (1,2) (1,0) (1,1) (1,2) (2,0) (2,1) (2,2) (2,0) (2,1) (2,2)
	 * (2,0) (2,1) (2,2) DOWN (0,0) (0,1) (0,2) (1,0) (1,1) (1,2) (2,0) (2,1)
	 * (2,2) REAR (0,0) (0,1) (0,2) (1,0) (1,1) (1,2) (2,0) (2,1) (2,2)
	 */
	private Color[][] UP;
	private Color[][] FRONT;
	private Color[][] LEFT;
	private Color[][] RIGHT;
	private Color[][] DOWN;
	private Color[][] REAR;

	private Color[][][] FACES; // all faces, in no particular order

	private int positionalMoves;
	private int effectiveMoves;

	public RubiksCube() {
		FACES = new Color[6][][];

		Color[] colors = Color.values();
		Color[][] face;

		for (int faceNum = 0; faceNum < 6; faceNum++) {
			face = new Color[3][3]; // lines x column

			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					face[ln][col] = colors[faceNum];
				}
			}

			FACES[faceNum] = face;
		}

		// this order is important and must be kept: FACES = {UP, LEFT, FRONT,
		// RIGHT, DOWN, REAR}
		UP = FACES[0];
		LEFT = FACES[1];
		FRONT = FACES[2];
		RIGHT = FACES[3];
		DOWN = FACES[4];
		REAR = FACES[5];

		positionalMoves = 0;
		effectiveMoves = 0;
	}

	public RubiksCube(RubiksCube other) {
		FACES = new Color[6][3][3];

		for (int faceNum = 0; faceNum < 6; faceNum++) {
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					FACES[faceNum][ln][col] = other.FACES[faceNum][ln][col];
				}
			}
		}

		// this order is important and must be kept: FACES = {UP, LEFT, FRONT,
		// RIGHT, DOWN, REAR}
		UP = FACES[0];
		LEFT = FACES[1];
		FRONT = FACES[2];
		RIGHT = FACES[3];
		DOWN = FACES[4];
		REAR = FACES[5];

		positionalMoves = other.positionalMoves;
		effectiveMoves = other.effectiveMoves;
	}

	public void setAllFaces(char[][][] cube) {
		int[] colorsCount = new int[6];
		Color color;

		for (int faceNum = 0; faceNum < 6; faceNum++) {
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					color = convertChar(cube[faceNum][ln][col]);
					this.FACES[faceNum][ln][col] = color;
					colorsCount[color.ordinal()]++;
				}
			}
		}

		for (int i = 0; i < colorsCount.length; i++) {
			if (colorsCount[i] != 9) {
				throw new Error("Color " + Color.values()[i] + " appears only " + colorsCount[i]
						+ " times in the cube!");
			}
		}

		positionalMoves = 0;
		effectiveMoves = 0;
	}

	private Color convertChar(char c) {
		switch (c) {
		case 'y':
		case 'Y':
			return Color.YELLOW;
		case 'r':
		case 'R':
			return Color.RED;
		case 'g':
		case 'G':
			return Color.GREEN;
		case 'o':
		case 'O':
			return Color.ORANGE;
		case 'w':
		case 'W':
			return Color.WHITE;
		case 'b':
		case 'B':
			return Color.BLUE;
		default:
			throw new Error("Invalid color - " + c);
		}
	}

	public int getAllMoves() {
		return positionalMoves + effectiveMoves;
	}

	public int getPositionalMoves() {
		return positionalMoves;
	}

	public int getEffectiveMoves() {
		return effectiveMoves;
	}

	/**
	 * Rotates the whole cube upward, around an imaginary horizontal axis (i.e.
	 * perpendicular to the left and right faces). The front face becomes the
	 * upper face and so on.
	 */
	public void moveUp() {
		Color[][] auxUp = UP;

		UP = FRONT;
		FRONT = DOWN;
		DOWN = REAR;
		REAR = auxUp;

		internalRotateFace(LEFT, false);
		internalRotateFace(RIGHT, true);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR }; // can
																		// be
																		// done
																		// more
																		// efficiently
		positionalMoves++;
	}

	private void internalRotateFace(Color[][] face, boolean clockWise) {
		Color[][] backupFace = new Color[3][3]; // TODO: tornar static?

		for (int ln = 0; ln < 3; ln++) {
			for (int col = 0; col < 3; col++) {
				backupFace[ln][col] = face[ln][col];
			}
		}

		if (clockWise) {
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					face[ln][col] = backupFace[2 - col][ln];
				}
			}

		} else {
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					face[ln][col] = backupFace[col][2 - ln];
				}
			}

		}
	}

	/**
	 * The opposite of moveUp().
	 * 
	 * @see #moveUp()
	 */
	public void moveDown() {
		Color[][] auxRear = REAR;

		REAR = DOWN;
		DOWN = FRONT;
		FRONT = UP;
		UP = auxRear;

		internalRotateFace(LEFT, true);
		internalRotateFace(RIGHT, false);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR };
		positionalMoves++;
	}

	/**
	 * Rotates the whole cube to the left, around an imaginary vertical axis
	 * (i.e. perpendicular to the upper and down face). The front face becomes
	 * the left face and so on.
	 */
	public void moveLeft() {
		Color[][] auxLeft = LEFT;

		LEFT = FRONT;
		FRONT = RIGHT;
		RIGHT = auxLeft;

		internalRotateFace(UP, true);
		internalRotateFace(DOWN, false);
		internalRotateFace(REAR, true);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR };
		positionalMoves++;
	}

	/**
	 * The opposite of moveLeft().
	 * 
	 * @see #moveLeft()
	 */
	public void moveRight() {
		Color[][] auxRight = RIGHT;

		RIGHT = FRONT;
		FRONT = LEFT;
		LEFT = auxRight;

		internalRotateFace(UP, false);
		internalRotateFace(DOWN, true);
		internalRotateFace(REAR, false);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR };
		positionalMoves++;
	}

	/**
	 * Rotates the whole cube in the clockwise direction, around an imaginary
	 * axis perpendicular to the front and rear faces. (The front face remains
	 * the front face, but the upper face becomes the right face, and so on).
	 */
	public void moveClockwise() {
		Color[][] auxRight = RIGHT;

		internalRotateFace(FRONT, true);
		internalRotateFace(REAR, false);

		RIGHT = UP;
		internalRotateFace(RIGHT, true);

		UP = LEFT;
		internalRotateFace(UP, true);

		LEFT = DOWN;
		internalRotateFace(LEFT, true);

		DOWN = auxRight;
		internalRotateFace(DOWN, true);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR };
		positionalMoves++;
	}

	/**
	 * The opposite of moveClockwise().
	 * 
	 * @see #moveClockwise()
	 */
	public void moveCounterClockwise() {
		Color[][] auxRight = RIGHT;

		internalRotateFace(FRONT, false);
		internalRotateFace(REAR, true);

		RIGHT = DOWN;
		internalRotateFace(RIGHT, false);

		DOWN = LEFT;
		internalRotateFace(DOWN, false);

		LEFT = UP;
		internalRotateFace(LEFT, false);

		UP = auxRight;
		internalRotateFace(UP, false);

		FACES = new Color[][][] { UP, LEFT, FRONT, RIGHT, DOWN, REAR };
		positionalMoves++;
	}

	public void spinUpperLineLeft() {
		Color[] frontLine = FRONT[0];

		FRONT[0] = RIGHT[0];

		RIGHT[0] = REAR[2];
		invert(RIGHT[0]);

		REAR[2] = LEFT[0];
		invert(REAR[2]);

		LEFT[0] = frontLine;

		internalRotateFace(UP, true);
		effectiveMoves++;
	}

	public void spinUpperLineRight() {
		Color[] frontLine = FRONT[0];

		FRONT[0] = LEFT[0];

		LEFT[0] = REAR[2];
		invert(LEFT[0]);

		REAR[2] = RIGHT[0];
		invert(REAR[2]);

		RIGHT[0] = frontLine;

		internalRotateFace(UP, false);
		effectiveMoves++;
	}

	private void invert(Color[] line) {
		Color aux = line[0];
		line[0] = line[2];
		line[2] = aux;
	}

	public void spinLowerLineLeft() {
		Color[] frontLine = FRONT[2];

		FRONT[2] = RIGHT[2];

		RIGHT[2] = REAR[0];
		invert(RIGHT[2]);

		REAR[0] = LEFT[2];
		invert(REAR[0]);

		LEFT[2] = frontLine;

		internalRotateFace(DOWN, false);
		effectiveMoves++;
	}

	public void spinLowerLineRight() {
		Color[] frontLine = FRONT[2];

		FRONT[2] = LEFT[2];

		LEFT[2] = REAR[0];
		invert(LEFT[2]);

		REAR[0] = RIGHT[2];
		invert(REAR[0]);

		RIGHT[2] = frontLine;

		internalRotateFace(DOWN, true);
		effectiveMoves++;
	}

	public void spinLeftColumnUp() {
		Color[] frontLeftCol = new Color[3];
		setLineFromColumn(frontLeftCol, FRONT, 0); // frontLeftCol = FRONT[][0]

		// FRONT[][0] = DOWN[][0]
		setColumnFromColumn(FRONT, 0, DOWN, 0);

		// DOWN[][0] = REAR[][0]
		setColumnFromColumn(DOWN, 0, REAR, 0);

		// REAR[][0] = UP[0]
		setColumnFromColumn(REAR, 0, UP, 0);

		// UP[0] = FRONT[0]
		setColumnFromLine(UP, 0, frontLeftCol);

		internalRotateFace(LEFT, false);
		effectiveMoves++;
	}

	private void setColumnFromColumn(Color[][] receivingFace, int colRcv, Color[][] sourceFace, int colSrc) {
		for (int i = 0; i < sourceFace.length; i++) {
			receivingFace[i][colRcv] = sourceFace[i][colSrc];
		}
	}

	public void spinLeftColumnDown() {
		Color[] frontLeftCol = new Color[3];
		setLineFromColumn(frontLeftCol, FRONT, 0); // frontLeftCol = FRONT[][0]

		// FRONT[][0] = UP[][0]
		setColumnFromColumn(FRONT, 0, UP, 0);

		// UP[][0] = REAR[][0]
		setColumnFromColumn(UP, 0, REAR, 0);

		// REAR[][0] = DOWN[0]
		setColumnFromColumn(REAR, 0, DOWN, 0);

		// DOWN[0] = FRONT[0]
		setColumnFromLine(DOWN, 0, frontLeftCol);

		internalRotateFace(LEFT, true);
		effectiveMoves++;
	}

	public void spinRightColumnUp() {
		Color[] frontRightCol = new Color[3];
		setLineFromColumn(frontRightCol, FRONT, 2); // frontRightCol =
													// FRONT[][2]

		// FRONT[][2] = DOWN[][2]
		setColumnFromColumn(FRONT, 2, DOWN, 2);

		// DOWN[][2] = REAR[][2]
		setColumnFromColumn(DOWN, 2, REAR, 2);

		// REAR[][2] = UP[2]
		setColumnFromColumn(REAR, 2, UP, 2);

		// UP[2] = FRONT[2]
		setColumnFromLine(UP, 2, frontRightCol);

		internalRotateFace(RIGHT, true);
		effectiveMoves++;

	}

	public void spinRightColumnDown() {
		Color[] frontRightCol = new Color[3];
		setLineFromColumn(frontRightCol, FRONT, 2); // frontRightCol =
													// FRONT[][2]

		// FRONT[][2] = UP[][2]
		setColumnFromColumn(FRONT, 2, UP, 2);

		// UP[][2] = REAR[][2]
		setColumnFromColumn(UP, 2, REAR, 2);

		// REAR[][2] = DOWN[2]
		setColumnFromColumn(REAR, 2, DOWN, 2);

		// DOWN[2] = FRONT[2]
		setColumnFromLine(DOWN, 2, frontRightCol);

		internalRotateFace(RIGHT, false);
		effectiveMoves++;
	}

	public void spinFrontLineClockwise() {
		Color[] frontLineInUpperFace = copy(UP[2]);

		// UP[2] = LEFT[*][2]
		setLineFromColumn(UP[2], LEFT, 2);

		// LEFT[*][2] = DOWN[0]
		setColumnFromLine(LEFT, 2, DOWN[0]);

		// DOWN[0] = invert(RIGHT[*][0])
		setLineFromColumn(DOWN[0], RIGHT, 0);
		invert(DOWN[0]);

		// RIGHT[*][0] = UP[2]
		setColumnFromLine(RIGHT, 0, frontLineInUpperFace);

		internalRotateFace(FRONT, true);
		effectiveMoves++;
	}

	public void spinFrontLineCounterClockwise() {
		Color[] frontLineInUpperFace = copy(UP[2]); // TODO: tornar static?

		// UP[2] = RIGHT[*][0]
		setLineFromColumn(UP[2], RIGHT, 0);

		// RIGHT[*][0] = invert(DOWN[0])
		invert(DOWN[0]);
		setColumnFromLine(RIGHT, 0, DOWN[0]);

		// DOWN[0] = LEFT[*][2]
		setLineFromColumn(DOWN[0], LEFT, 2);

		// LEFT[*][2] = invert(UP[2])
		invert(frontLineInUpperFace);
		setColumnFromLine(LEFT, 2, frontLineInUpperFace);

		internalRotateFace(FRONT, false);
		effectiveMoves++;
	}

	private Color[] copy(Color[] line) {
		Color[] newLine = new Color[line.length];
		for (int i = 0; i < newLine.length; i++) {
			newLine[i] = line[i];
		}
		return newLine;
	}

	private void setLineFromColumn(Color[] line, Color[][] face, int column) {
		for (int i = 0; i < line.length; i++) {
			line[i] = face[i][column];
		}
	}

	private void setColumnFromLine(Color[][] face, int column, Color[] line) {
		for (int i = 0; i < line.length; i++) {
			face[i][column] = line[i];
		}
	}

	// obs.: clockwise when looking to the front face
	public void spinRearLineClockwise() {
		Color[] rearLineInUpperFace = copy(UP[0]);

		// UP[0] = invert(LEFT[*][0])
		setLineFromColumn(UP[0], LEFT, 0);
		invert(UP[0]);

		// LEFT[*][0] = DOWN[2]
		setColumnFromLine(LEFT, 0, DOWN[2]);

		// DOWN[2] = invert(RIGHT[*][2])
		setLineFromColumn(DOWN[2], RIGHT, 2);
		invert(DOWN[2]);

		// RIGHT[*][2] = UP[0]
		setColumnFromLine(RIGHT, 2, rearLineInUpperFace);

		internalRotateFace(REAR, false);
		effectiveMoves++;
	}

	public void spinRearLineCounterClockwise() {
		Color[] rearLineInUpperFace = copy(UP[0]);

		// UP[0] = RIGHT[*][2])
		setLineFromColumn(UP[0], RIGHT, 2);

		// RIGHT[*][2] = invert(DOWN[2])
		invert(DOWN[2]);
		setColumnFromLine(RIGHT, 2, DOWN[2]);

		// DOWN[2] = LEFT[*][0]
		setLineFromColumn(DOWN[2], LEFT, 0);

		// LEFT[*][0] = invert(UP[0])
		invert(rearLineInUpperFace);
		setColumnFromLine(LEFT, 0, rearLineInUpperFace);

		internalRotateFace(REAR, true);
		effectiveMoves++;
	}

	public boolean isSolution() {
		Color color;

		for (int faceNum = 0; faceNum < 6; faceNum++) {
			color = this.FACES[faceNum][0][0];

			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					if (this.FACES[faceNum][ln][col] != color) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public void applyAction(Actions action) {
		switch (action) {
		case MOVE_RIGHT:
			this.moveRight();
			break;
		case MOVE_LEFT:
			this.moveLeft();
			break;
		case MOVE_UP:
			this.moveUp();
			break;
		case MOVE_DOWN:
			moveDown();
			break;
		case MOVE_CLOCKWISE:
			moveClockwise();
			break;
		case MOVE_COUNTER_CLOCKWISE:
			moveCounterClockwise();
			break;
		case SPIN_UPPER_LINE_LEFT:
			spinUpperLineLeft();
			break;
		case SPIN_UPPER_LINE_RIGHT:
			spinUpperLineRight();
			break;
		case SPIN_LOWER_LINE_LEFT:
			spinLowerLineLeft();
			break;
		case SPIN_LOWER_LINE_RIGHT:
			spinLowerLineRight();
			break;
		case SPIN_LEFT_COLUMN_DOWN:
			spinLeftColumnDown();
			break;
		case SPIN_LEFT_COLUMN_UP:
			spinLeftColumnUp();
			break;
		case SPIN_RIGHT_COLUMN_DOWN:
			spinRightColumnDown();
			break;
		case SPIN_RIGHT_COLUMN_UP:
			spinRightColumnUp();
			break;
		case SPIN_FRONT_LINE_CW:
			spinFrontLineClockwise();
			break;
		case SPIN_FRONT_LINE_COUNTER_CW:
			spinFrontLineCounterClockwise();
			break;
		case SPIN_REAR_LINE_CW:
			spinRearLineClockwise();
			break;
		case SPIN_REAR_LINE_COUNTER_CW:
			spinRearLineCounterClockwise();
			break;
		default:
			throw new Error("Unexpected value: " + action);
		}
	}

	private static Color[][] blankFace = { { Color.none, Color.none, Color.none },
			{ Color.none, Color.none, Color.none }, { Color.none, Color.none, Color.none } };

	public String toString() {
		StringBuilder builder = new StringBuilder();

		showFaces(builder, blankFace, UP);
		showFaces(builder, LEFT, FRONT, RIGHT);
		showFaces(builder, blankFace, DOWN);
		showFaces(builder, blankFace, REAR);

		return builder.toString();
	}

	private void showFaces(StringBuilder builder, Color[][]... faces) {
		for (int ln = 0; ln < 3; ln++) {
			for (int faceNum = 0; faceNum < faces.length; faceNum++) {
				for (int col = 0; col < 3; col++) {
					builder.append(faces[faceNum][ln][col].rep);
					builder.append(' ');
				}
			}
			builder.append('\n');
		}
	}

	@Override
	public boolean equals(Object o) {
		// verifica��o muito simples, sem considerar movimentos posicionais
		if (!(o instanceof RubiksCube)) {
			return false;
		}
		RubiksCube other = (RubiksCube) o;

		for (int fc = 0; fc < FACES.length; fc++) {
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					if (FACES[fc][ln][col] != other.FACES[fc][ln][col]) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public int hash = -1;

	@Override
	public int hashCode() {
		if (hash == -1) {
			// soma os hashcodes da face frontal
			long sum = 0;
			for (int ln = 0; ln < 3; ln++) {
				for (int col = 0; col < 3; col++) {
					sum += (long) FRONT[ln][col].hashCode();
				}
			}
			hash = (int) (sum % Integer.MAX_VALUE);
		}
		return hash;
	}

}
