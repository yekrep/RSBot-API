package org.powerbot.client;

public class ModelCapture implements AbstractModel {
	private int[] vertex_x;
	private int[] vertex_y;
	private int[] vertex_z;
	private short[] face_a;
	private short[] face_b;
	private short[] face_c;
	private int vertices;
	private int faces;

	private ModelCapture() {
		reset();
	}

	public static AbstractModel updateModel(final AbstractModel model, AbstractModel stored) {
		if (stored == null || !(stored instanceof ModelCapture)) {
			stored = new ModelCapture();
		}
		((ModelCapture) stored).update(model);
		return stored;
	}

	private void update(final AbstractModel model) {
		if (model == null || !(model instanceof JavaModel)) {
			reset();
			return;
		}

		final int[] x;
		final int[] y;
		final int[] z;
		final short[] a;
		final short[] b;
		final short[] c;
		x = model.getXPoints();
		y = model.getYPoints();
		z = model.getZPoints();
		a = model.getIndices1();
		b = model.getIndices2();
		c = model.getIndices3();
		final int vertices = Math.min(x.length, Math.min(y.length, z.length));
		final int faces = Math.min(a.length, Math.min(b.length, c.length));
		if (vertices > this.vertices) {
			vertex_x = x.clone();
			vertex_y = y.clone();
			vertex_z = z.clone();
			this.vertices = vertices;
		} else {
			this.vertices = vertices;
			System.arraycopy(x, 0, vertex_x, 0, vertices);
			System.arraycopy(y, 0, vertex_y, 0, vertices);
			System.arraycopy(z, 0, vertex_z, 0, vertices);
		}
		if (faces > this.faces) {
			face_a = a.clone();
			face_b = b.clone();
			face_c = c.clone();
			this.faces = faces;
		} else {
			this.faces = faces;
			System.arraycopy(a, 0, face_a, 0, faces);
			System.arraycopy(b, 0, face_b, 0, faces);
			System.arraycopy(c, 0, face_c, 0, faces);
		}
	}

	private void reset() {
		vertex_x = vertex_y = vertex_z = new int[0];
		face_a = face_b = face_c = new short[0];
		vertices = faces = 0;
	}

	public int[] getXPoints() {
		return vertex_x;
	}

	public int[] getYPoints() {
		return vertex_y;
	}

	public int[] getZPoints() {
		return vertex_z;
	}

	@Override
	public short[] getIndices1() {
		return face_a;
	}

	@Override
	public short[] getIndices2() {
		return face_b;
	}

	@Override
	public short[] getIndices3() {
		return face_c;
	}

	public int getVertices() {
		return vertices;
	}

	public int getFaces() {
		return faces;
	}
}
