package com.innotech.imap_taxi.graph_utils;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class GraphUtils {

	private final static float[][] colorsMatrixOrderItem = {
			{ 255, 255, 255, 0.4f }, { 222, 231, 241, 0.4f },
			{ 186, 204, 225, 0.41f }, { 154, 180, 211, 0.41f },
			{ 126, 160, 200, 0.42f }, { 104, 144, 190, 0.43f },
			{ 87, 131, 183, 0.44f }, { 76, 122, 178, 0.45f },
			{ 69, 117, 175, 0.46f }, { 67, 116, 174, 0.5f },
			{ 66, 111, 167, 0.5f }, { 60, 91, 139, 0.5f },
			{ 55, 75, 116, 0.5f }, { 51, 62, 99, 0.5f }, { 48, 54, 86, 0.5f },
			{ 46, 48, 79, 0.5f }, { 46, 47, 77, 0.5f }, { 48, 49, 79, 0.5f },
			{ 55, 56, 84, 0.5f }, { 66, 67, 94, 0.5f }, { 83, 84, 109, 0.5f },
			{ 105, 106, 128, 0.5f }, { 133, 133, 151, 0.5f },
			{ 165, 165, 178, 0.5f }, { 203, 203, 211, 0.5f },
			{ 244, 244, 246, 0.5f }, { 255, 255, 255, 0.5f } };


	private final static float[] orderItemMatrixPoints = { 0f, 0.3f, 0.7f,
			1.15f, 1.65f, 2.22f, 2.87f, 3.69f, 4.83f, 8f, 9.4f, 16.36f, 24.24f,
			33.26f, 44.14f, 58.76f, 93.08f, 95.77f, 96.73f, 97.42f, 97.98f,
			98.46f, 98.88f, 99.26f, 99.61f, 99.92f, 100f };

	private static float[][] buttonColorMatrix = { { 85, 224, 255, 1 },
			{ 88, 224, 255, 1 }, { 132, 233, 255, 0.91f },
			{ 170, 239, 255, 0.82f }, { 201, 245, 255, 0.73f },
			{ 225, 249, 255, 0.64f }, { 242, 253, 255, 0.54f },
			{ 252, 254, 255, 0.43f }, { 255, 255, 255, 0.3f },
			{ 210, 222, 235, 0.31f }, { 144, 173, 207, 0.32f },
			{ 103, 143, 189, 0.33f }, { 87, 131, 182, 0.33f },
			{ 76, 122, 177, 0.36f }, { 69, 117, 175, 0.4f },
			{ 67, 116, 174, 0.5f }, { 66, 113, 170, 0.5f },
			{ 59, 89, 137, 0.5f }, { 53, 71, 110, 0.5f }, { 49, 58, 92, 0.5f },
			{ 47, 50, 81, 0.5f }, { 46, 47, 77, 0.5f }, { 46, 49, 79, 0.5f },
			{ 48, 56, 86, 0.5f }, { 50, 67, 97, 0.5f }, { 54, 84, 114, 0.5f },
			{ 59, 106, 136, 0.5f }, { 65, 133, 164, 0.5f },
			{ 72, 165, 196, 0.5f }, { 80, 201, 232, 0.5f },
			{ 85, 224, 255, 0.5f } };

	private final static float[] buttonMatrixPoints = { 19.1f, 19.14f, 19.88f,
			20.65f, 21.44f, 22.27f, 23.14f, 24.09f, 25.26f, 25.32f, 25.41f,
			25.48f, 25.51f, 26.97f, 29.08f, 34.93f, 35.57f, 41.93f, 48.44f,
			55.1f, 62f, 69.49f, 72.71f, 73.87f, 74.7f, 75.36f, 75.94f, 76.44f,
			76.9f, 77.31f, 77.53f };

	private static float[][] ETHER_NUMBER_BUTTONS_COLOR_MATRIX = {
			{ 255, 255, 255, 0.5f }, { 244, 244, 246, 0.5f },
			{ 203, 203, 211, 0.5f }, { 165, 165, 178, 0.5f },
			{ 133, 133, 151, 0.5f }, { 105, 106, 128, 0.5f },
			{ 83, 84, 109, 0.5f }, { 66, 67, 94, 0.5f }, { 55, 56, 84, 0.5f },
			{ 48, 49, 79, 0.5f }, { 46, 47, 77, 0.5f }, { 46, 47, 77, 0.8f },
			{ 46, 47, 77, 0.78f }, { 47, 50, 81, 0.75f },
			{ 50, 59, 94, 0.71f }, { 54, 75, 116, 0.67f },
			{ 61, 96, 146, 0.63f }, { 67, 116, 174, 0.6f },
			{ 69, 117, 175, 0.52f }, { 76, 122, 178, 0.49f },
			{ 87, 131, 183, 0.47f }, { 104, 144, 190, 0.46f },
			{ 126, 160, 200, 0.44f }, { 154, 180, 211, 0.43f },
			{ 186, 204, 225, 0.42f }, { 222, 231, 241, 0.41f },
			{ 255, 255, 255, 0.4f } };

	private final static float[] ETHER_NUMBER_BUTTONS_POINTS = { 19.1f, 19.21f,
			19.69f, 20.22f, 20.79f, 21.43f, 22.15f, 22.99f, 24.03f, 25.48f,
			29.52f, 45.94f, 64.72f, 67.23f, 69.94f, 72.75f, 75.61f, 77.77f,
			86.58f, 89.75f, 92.01f, 93.84f, 95.41f, 96.79f, 98.05f, 99.16f,
			100f };

	private static float[][] ETHER_ROUTE_COLOR_MATRIX = {
			{ 217, 217, 217, 0.5f }, { 211, 211, 212, 0.5f },
			{ 171, 170, 176, 0.53f }, { 134, 132, 144, 0.55f },
			{ 102, 100, 116, 0.58f }, { 75, 72, 92, 0.61f },
			{ 54, 50, 73, 0.64f }, { 37, 33, 59, 0.68f },
			{ 25, 22, 48, 0.73f }, /* { 19, 15, 43, 0.81f }, { 17, 13, 41, 1 }, */
			{ 45, 42, 60, 0.84f }, { 67, 65, 75, 0.67f },
			{ 83, 82, 86, 0.49f }, { 92, 92, 92, 0.29f },
			/* { 95, 95, 94, 0.03f }, { 96, 96, 95, 0.03f }, */
			{ 98, 98, 97, 0.43f }, { 105, 105, 104, 0.57f },
			{ 116, 116, 115, 0.67f }, { 133, 133, 132, 0.75f },
			{ 155, 155, 154, 0.82f }, { 182, 182, 182, 0.89f },
			{ 214, 214, 214, 0.94f }, { 250, 250, 250, 0.99f },
			{ 255, 255, 255, 1 } };

	private final static float[] ETHER_ROUTE_POINTS = { 20.6f, 20.6f, 20.62f,
			20.64f, 20.67f, 20.69f, 20.73f, 20.76f, 20.8f, /* 20.87f, 21.04f, */
			21.76f, 22.54f, 23.37f, 24.29f, /* 25.47f, 74.85f, */76.56f,
			77.18f, 77.61f, 77.97f, 78.27f, 78.54f, 78.78f, 79f, 79.03f };

	public static Drawable buttonStyle(final Button btn) {
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0,
						btn.getHeight() * 1.36f, 0, -btn.getHeight() * 0.35f,
						convertColorToHex(buttonColorMatrix),
						normalizePointsMatrix(buttonMatrixPoints),
						Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		return p;
	}

	// not used
	public static Drawable getEtherGradient(final LinearLayout view) {
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0, view.getHeight(), 0,
						0, new int[] { Color.parseColor("#FF110D29"),
								Color.parseColor("#66110D29"),
								Color.parseColor("#4060605F"),
								Color.parseColor("#005783B6"), }, new float[] {
								0, 0.70f, 0.9f, 1 }, Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		return p;
	}

	public static Drawable getOrderItemGradient(final View view) {
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0, 0, 0,
						view.getHeight(),
						convertColorToHex(colorsMatrixOrderItem),
						normalizePointsMatrix(orderItemMatrixPoints),
						Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		return p;
	}

	public static Drawable getEtherButtonsGradient(final Button view) {
		// 50% 134.74% ,50% -36.91%
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0,
						view.getHeight() * 1.3474f, 0,
						-view.getHeight() * 0.36f,
						convertColorToHex(ETHER_NUMBER_BUTTONS_COLOR_MATRIX),
						normalizePointsMatrix(ETHER_NUMBER_BUTTONS_POINTS),
						Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		return p;
	}

	public static Drawable getEtherRouteGradient(final LinearLayout view) {
		// Linear,50% 134.74% ,50% -36.91%
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0,
						view.getHeight() * 1.3474f, 0,
						-view.getHeight() * 0.369f,
						convertColorToHex(ETHER_ROUTE_COLOR_MATRIX),
						normalizePointsMatrix(ETHER_ROUTE_POINTS),
						Shader.TileMode.REPEAT);
				return lg;
			}
		};
		PaintDrawable p = new PaintDrawable();
		p.setShape(new RectShape());
		p.setShaderFactory(sf);
		return p;
	}

	private static int[] convertColorToHex(float[][] colorsMatrix) {
		int[] colorsHex = new int[colorsMatrix.length];
		for (int i = 0; i < colorsMatrix.length; i++) {
			/*
			 * Log.d("myLogs", i + " - color = " + "#" +
			 * Integer.toHexString(Math.round(colorsMatrix[i][3] * 255)) +
			 * Integer.toHexString((int) colorsMatrix[i][0]) +
			 * Integer.toHexString((int) colorsMatrix[i][1]) +
			 * Integer.toHexString((int) colorsMatrix[i][2]));
			 */
			colorsHex[i] = Color.parseColor("#"
					+ Integer.toHexString(Math.round(colorsMatrix[i][3] * 255))
					+ Integer.toHexString((int) colorsMatrix[i][0])
					+ Integer.toHexString((int) colorsMatrix[i][1])
					+ Integer.toHexString((int) colorsMatrix[i][2]));

		}

		return colorsHex;
	}

	private static float[] normalizePointsMatrix(float[] points) {
		float[] pointsMatrix = new float[points.length];
		for (int i = 0; i < points.length; i++)
			pointsMatrix[i] = points[i] / 100;
		return pointsMatrix;
	}

}
