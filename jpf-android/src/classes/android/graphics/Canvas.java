package android.graphics;



public class Canvas {
	private static final int MAXMIMUM_BITMAP_SIZE = 32766;

	public Canvas() {
	}

	public Canvas(Bitmap bitmap) {
	}

	Canvas(int nativeCanvas) {
	}

	public boolean isHardwareAccelerated() {
		return false;
	}

	public void setBitmap(Bitmap bitmap) {
	}

	public void setViewport(int width, int height) {
	}

	public boolean isOpaque() {
		return false;
	}

	public int getWidth() {
		return 1;
	}

	public int getHeight() {
		return 1;
	}

	public int getDensity() {
		return 0;
	}

	public void setDensity(int density) {
	}

	public void setScreenDensity(int density) {
	}

	public int getMaximumBitmapWidth() {
		return MAXMIMUM_BITMAP_SIZE;
	}

	/**
	 * Returns the maximum allowed height for bitmaps drawn with this canvas.
	 * Attempting to draw with a bitmap taller than this value will result in an
	 * error.
	 * 
	 * @see #getMaximumBitmapWidth()
	 */
	public int getMaximumBitmapHeight() {
		return MAXMIMUM_BITMAP_SIZE;
	}

	// the SAVE_FLAG constants must match their native equivalents

	/** restore the current matrix when restore() is called */
	public static final int MATRIX_SAVE_FLAG = 0x01;
	/** restore the current clip when restore() is called */
	public static final int CLIP_SAVE_FLAG = 0x02;
	/** the layer needs to per-pixel alpha */
	public static final int HAS_ALPHA_LAYER_SAVE_FLAG = 0x04;
	/** the layer needs to 8-bits per color component */
	public static final int FULL_COLOR_LAYER_SAVE_FLAG = 0x08;
	/** clip against the layer's bounds */
	public static final int CLIP_TO_LAYER_SAVE_FLAG = 0x10;
	/** restore everything when restore() is called */
	public static final int ALL_SAVE_FLAG = 0x1F;

	public int save() {
		return 1;
	};

	public int save(int saveFlags) {
		return 1;
	};

	public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
		return 1;
	}

	public int saveLayer(float left, float top, float right, float bottom,
			Paint paint, int saveFlags) {
		return 1;
	}

	public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
		return 1;
	}

	public int saveLayerAlpha(float left, float top, float right, float bottom,
			int alpha, int saveFlags) {
		return 1;
	}

	public void restore() {
	}

	public int getSaveCount() {
		return 1;
	}

	public void restoreToCount(int saveCount) {
	}

	public void translate(float dx, float dy) {
	}

	public void scale(float sx, float sy) {
	}

	public final void scale(float sx, float sy, float px, float py) {
	}

	public void rotate(float degrees) {
	}

	public final void rotate(float degrees, float px, float py) {
	}

	public void skew(float sx, float sy) {
	}

	public void concat(Matrix matrix) {
	}

	public void setMatrix(Matrix matrix) {
	}

	public void getMatrix(Matrix ctm) {
	}

	public final Matrix getMatrix() {
		Matrix m = new Matrix();
		return m;
	}

	public boolean clipRect(RectF rect, Region.Op op) {
		return false;
	}

	public boolean clipRect(Rect rect, Region.Op op) {
		return false;
	}

	public boolean clipRect(RectF rect) {
		return false;
	}

	public boolean clipRect(Rect rect) {
		return false;
	}

	public boolean clipRect(float left, float top, float right, float bottom,
			Region.Op op) {
		return false;
	}

	public boolean clipRect(float left, float top, float right, float bottom) {
		return false;
	}

	public boolean clipRect(int left, int top, int right, int bottom) {
		return false;
	}

	public boolean clipPath(Path path, Region.Op op) {
		return false;
	}

	public boolean clipPath(Path path) {
		return false;
	}

	public boolean clipRegion(Region region, Region.Op op) {
		return false;
	}

	public boolean clipRegion(Region region) {
		return false;
	}

	public DrawFilter getDrawFilter() {
		return new DrawFilter();
	}

	public void setDrawFilter(DrawFilter filter) {
	}

	public enum EdgeType {
		BW(0), // !< treat edges by just rounding to nearest pixel boundary
		AA(1); // !< treat edges by rounding-out, since they may be antialiased

		EdgeType(int nativeInt) {
			this.nativeInt = nativeInt;
		}

		/**
		 * @hide
		 */
		public final int nativeInt;
	}

	public boolean quickReject(RectF rect, EdgeType type) {
		return false;
	}

	public boolean quickReject(Path path, EdgeType type) {
		return false;
	}

	public boolean quickReject(float left, float top, float right,
			float bottom, EdgeType type) {
		return false;
	}

	public boolean getClipBounds(Rect bounds) {
		return true;
	}

	public final Rect getClipBounds() {
		return new Rect();
	}

	public void drawRGB(int r, int g, int b) {
	}

	public void drawARGB(int a, int r, int g, int b) {
	}

	public void drawColor(int color) {
	}

	public void drawColor(int color, PorterDuff.Mode mode) {
	}

	public void drawPaint(Paint paint) {
	}

	public void drawPoints(float[] pts, int offset, int count, Paint paint) {
	}

	public void drawPoints(float[] pts, Paint paint) {
	}

	public void drawPoint(float x, float y, Paint paint) {

	}

	public void drawLine(float startX, float startY, float stopX, float stopY,
			Paint paint) {
	}

	public native void drawLines(float[] pts, int offset, int count, Paint paint);

	public void drawLines(float[] pts, Paint paint) {
	}

	public void drawRect(RectF rect, Paint paint) {
	}

	public void drawRect(Rect r, Paint paint) {
	}

	public void drawRect(float left, float top, float right, float bottom,
			Paint paint) {
	}

	public void drawOval(RectF oval, Paint paint) {
	}

	public void drawCircle(float cx, float cy, float radius, Paint paint) {
	}

	public void drawArc(RectF oval, float startAngle, float sweepAngle,
			boolean useCenter, Paint paint) {
	}

	public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
	}

	public void drawPath(Path path, Paint paint) {
	}

	private static void throwIfRecycled(Bitmap bitmap) {
	}

	public void drawPatch(Bitmap bitmap, byte[] chunks, RectF dst, Paint paint) {
	}

	public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
		throwIfRecycled(bitmap);
	}

	public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
	}

	public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
	}

	public void drawBitmap(int[] colors, int offset, int stride, float x,
			float y, int width, int height, boolean hasAlpha, Paint paint) {
	}

	public void drawBitmap(int[] colors, int offset, int stride, int x, int y,
			int width, int height, boolean hasAlpha, Paint paint) {
	}

	public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
	}

	protected static void checkRange(int length, int offset, int count) {
	}

	public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight,
			float[] verts, int vertOffset, int[] colors, int colorOffset,
			Paint paint) {
	}

	public enum VertexMode {
		TRIANGLES(0), TRIANGLE_STRIP(1), TRIANGLE_FAN(2);

		VertexMode(int nativeInt) {
			this.nativeInt = nativeInt;
		}

		/**
		 * @hide
		 */
		public final int nativeInt;
	}

	public void drawVertices(VertexMode mode, int vertexCount, float[] verts,
			int vertOffset, float[] texs, int texOffset, int[] colors,
			int colorOffset, short[] indices, int indexOffset, int indexCount,
			Paint paint) {
	}

	public void drawText(char[] text, int index, int count, float x, float y,
			Paint paint) {
	}

	public void drawText(String text, float x, float y, Paint paint) {
	}

	public void drawText(String text, int start, int end, float x, float y,
			Paint paint) {
	}

	public void drawText(CharSequence text, int start, int end, float x,
			float y, Paint paint) {
	}

	public void drawTextRun(char[] text, int index, int count,
			int contextIndex, int contextCount, float x, float y, int dir,
			Paint paint) {

	}

	public void drawTextRun(CharSequence text, int start, int end,
			int contextStart, int contextEnd, float x, float y, int dir,
			Paint paint) {

	}

	public void drawPosText(char[] text, int index, int count, float[] pos,
			Paint paint) {
	}

	public void drawPosText(String text, float[] pos, Paint paint) {
	}

	public void drawTextOnPath(char[] text, int index, int count, Path path,
			float hOffset, float vOffset, Paint paint) {
	}

	public void drawTextOnPath(String text, Path path, float hOffset,
			float vOffset, Paint paint) {
	}

	public void drawPicture(Picture picture) {
	}

	public void drawPicture(Picture picture, RectF dst) {
	}

	public void drawPicture(Picture picture, Rect dst) {
	}

	public static void freeCaches() {
	}

}
