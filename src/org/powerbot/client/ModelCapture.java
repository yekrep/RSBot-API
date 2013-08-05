package org.powerbot.client;

public class ModelCapture implements AbstractModel {
	private int[] vertex_x;
	private int[] vertex_y;
	private int[] vertex_z;

	private short[] face_a;
	private short[] face_b;
	private short[] face_c;
	private int numVertices;
	private int numFaces;

	private ModelCapture(AbstractModel abstractModel) {
		if (abstractModel == null) {
			return;
		}

		if (abstractModel instanceof JavaModel) {
			JavaModel model = (JavaModel) abstractModel;
			vertex_x = model.getXPoints().clone();
			vertex_y = model.getYPoints().clone();
			vertex_z = model.getZPoints().clone();
			face_a = model.getIndices1().clone();
			face_b = model.getIndices2().clone();
			face_c = model.getIndices3().clone();
		} else if (abstractModel instanceof GLModel) {
			GLModel model = (GLModel) abstractModel;
			vertex_x = model.getXPoints().clone();
			vertex_y = model.getYPoints().clone();
			vertex_z = model.getZPoints().clone();
			short[][] data = extract(model);
			face_a = data[0];
			face_b = data[1];
			face_c = data[2];
		} else {
			return;
		}
		numVertices = Math.min(vertex_x.length, Math.min(vertex_y.length, vertex_z.length));
		numFaces = Math.min(face_a.length, Math.min(face_b.length, face_c.length));
	}

	public static AbstractModel updateModel(AbstractModel game, AbstractModel capture) {
		if (capture == null || !(capture instanceof ModelCapture)) {
			capture = new ModelCapture(game);
			return capture;
		}
		final ModelCapture reused_capture = (ModelCapture) capture;
		reused_capture.update(game);
		return reused_capture;
	}

	private void update(AbstractModel abstractModel) {
		if (abstractModel == null) {
			return;
		}

		int[] vertices_x;
		int[] vertices_y;
		int[] vertices_z;
		short[] indices1;
		short[] indices2;
		short[] indices3;

		if (abstractModel instanceof JavaModel) {
			JavaModel model = (JavaModel) abstractModel;
			vertices_x = model.getXPoints().clone();
			vertices_y = model.getYPoints().clone();
			vertices_z = model.getZPoints().clone();
			indices1 = model.getIndices1().clone();
			indices2 = model.getIndices2().clone();
			indices3 = model.getIndices3().clone();
		} else if (abstractModel instanceof GLModel) {
			GLModel model = (GLModel) abstractModel;
			vertices_x = model.getXPoints().clone();
			vertices_y = model.getYPoints().clone();
			vertices_z = model.getZPoints().clone();
			short[][] data = extract(model);
			indices1 = data[0];
			indices2 = data[1];
			indices3 = data[2];
		} else {
			return;
		}

		final int numVertices = Math.min(vertices_x.length, Math.min(vertices_y.length, vertices_z.length));
		final int numFaces = Math.min(indices1.length, Math.min(indices2.length, indices3.length));
		if (numVertices > this.numVertices) {
			this.numVertices = numVertices;
			vertex_x = vertices_x.clone();
			vertex_y = vertices_y.clone();
			vertex_z = vertices_z.clone();
		} else {
			this.numVertices = numVertices;
			System.arraycopy(vertices_x, 0, vertex_x, 0, numVertices);
			System.arraycopy(vertices_y, 0, vertex_y, 0, numVertices);
			System.arraycopy(vertices_z, 0, vertex_z, 0, numVertices);
		}

		if (numFaces > this.numFaces) {
			this.numFaces = numFaces;
			face_a = indices1.clone();
			face_b = indices2.clone();
			face_c = indices3.clone();
		} else {
			this.numFaces = numFaces;
			System.arraycopy(indices1, 0, face_a, 0, numFaces);
			System.arraycopy(indices2, 0, face_b, 0, numFaces);
			System.arraycopy(indices3, 0, face_c, 0, numFaces);
		}
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

	public short[] getFaceA() {
		return face_a;
	}

	public short[] getFaceB() {
		return face_b;
	}

	public short[] getFaceC() {
		return face_c;
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getNumFaces() {
		return numFaces;
	}

	public short[][] extract(GLModel model) {
		GLTriangle[] triangles = model.getTriangles();
		if (triangles != null) {
			int len = triangles.length;
			short[][] arr = new short[3][len];
			for (int i = 0; i < len; i++) {
				GLTriangle triangle = triangles[i];
				arr[0][i] = (short) triangle.getAPoint();
				arr[1][i] = (short) triangle.getBPoint();
				arr[2][i] = (short) triangle.getCPoint();
			}
			return arr;
		}
		return new short[3][0];
	}
}
