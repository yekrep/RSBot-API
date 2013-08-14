package org.powerbot.client;

public class ModelCapture implements AbstractModel {
	private int[] vertex_x;
	private int[] vertex_y;
	private int[] vertex_z;
	private int[] face_a;
	private int[] face_b;
	private int[] face_c;
	private int vertices;
	private int faces;

	private ModelCapture() {
		reset();
	}

	public static AbstractModel updateModel(AbstractModel model, AbstractModel stored) {
		if (stored == null || !(stored instanceof ModelCapture)) {
			stored = new ModelCapture();
		}
		((ModelCapture) stored).update(model);
		return stored;
	}

	private void update(AbstractModel abstractModel) {
		if (abstractModel == null) {
			reset();
			return;
		}

		int[] x, y, z;
		int[] a, b, c;
		if (abstractModel instanceof JavaModel) {
			JavaModel model = (JavaModel) abstractModel;
			x = model.getXPoints();
			y = model.getYPoints();
			z = model.getZPoints();
			a = ints(model.getIndices1());
			b = ints(model.getIndices2());
			c = ints(model.getIndices3());
		} else if (abstractModel instanceof GLModel) {
			GLModel model = (GLModel) abstractModel;
			x = model.getXPoints();
			y = model.getYPoints();
			z = model.getZPoints();
			if (x == null) {
				x = new int[0];
			}
			if (y == null) {
				y = new int[0];
			}
			if (z == null) {
				z = new int[0];
			}
			GLTriangle[] triangles = model.getTriangles();
			a = b = c = new int[0];
			if (triangles != null) {
				int len = triangles.length;
				a = new int[len];
				b = new int[len];
				c = new int[len];
				for (int i = 0; i < len; i++) {
					GLTriangle triangle = triangles[i];
					if (triangle == null) {
						continue;
					}
					a[i] = triangle.getAPoint();
					b[i] = triangle.getBPoint();
					c[i] = triangle.getCPoint();
				}
			}
		} else {
			x = y = z = new int[0];
			a = b = c = new int[0];
		}
		int vertices = Math.max(x.length, Math.min(y.length, z.length));
		int faces = Math.min(a.length, Math.min(b.length, c.length));
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
		face_a = face_b = face_c = new int[0];
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

	public int[] getFaceA() {
		return face_a;
	}

	public int[] getFaceB() {
		return face_b;
	}

	public int[] getFaceC() {
		return face_c;
	}

	public int getVertices() {
		return vertices;
	}

	public int getFaces() {
		return faces;
	}

	private int[] ints(short[] shorts) {
		int len = shorts.length;
		int[] arr = new int[len];
		for (int i = 0; i < len; i++) {
			arr[i] = shorts[i];
		}
		return arr;
	}
}
