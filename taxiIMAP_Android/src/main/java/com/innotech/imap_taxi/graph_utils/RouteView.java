package com.innotech.imap_taxi.graph_utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class RouteView extends View {
	int routeType = 0;
	int parentWidth;
	int parentHeight;
	Context mContext;
	View parent;
	RelativeLayout parentLayout;

	public void setRouteType(int routeType) {
		this.routeType = routeType;
	}

	public void setParentWidth(int parentWidth) {
		this.parentWidth = parentWidth;
	}

	public void setParentHeight(int parentHeight) {
		this.parentHeight = parentHeight;
		invalidate();
	}

	Paint dotFromPaint = new Paint();
	Paint dotToPaint = new Paint();
	Paint routePaint = new Paint();
	Paint routeCut = new Paint();

	public final static int ONE_POINT_ROUTE = 1;
	public final static int TWO_POINTS_ROUTE = 2;
	public final static int MANY_POINTS_ROUTE = 3;

	private float dotRadius;
	private float lineHalfWidth;

	public RouteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public void init() {

		this.dotFromPaint.setColor(Color.parseColor("#04742E"));
		this.dotFromPaint.setStrokeWidth(2);
		this.dotToPaint.setColor(Color.parseColor("#881D32"));
		this.dotToPaint.setStrokeWidth(2);
		this.routePaint.setColor(Color.parseColor("#6F6F6E"));
		this.routePaint.setStrokeWidth(2);
		this.routeCut.setColor(Color.parseColor("#6F6F6E"));
		this.routeCut.setStyle(Style.STROKE);
		this.routeCut.setStrokeWidth(lineHalfWidth);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		this.parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.dotRadius = Math.round(this.parentWidth / 3);
		this.lineHalfWidth = Math.round(dotRadius / 4);
		setMeasuredDimension(this.parentWidth, this.parentHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBackgound(canvas);
		float lineHeight = this.parentHeight / 2 - dotRadius * 2.2f;
		Path cutPath = new Path();
		this.routeCut.setStyle(Style.STROKE);
		this.routeCut.setStrokeWidth(lineHalfWidth);
		switch (routeType) {
		case ONE_POINT_ROUTE:
			canvas.drawRect((float) (parentWidth / 2) - lineHalfWidth,
					(float) (dotRadius * 1.2), (float) (parentWidth / 2)
							+ lineHalfWidth, (float) (dotRadius * 1.2)
							+ lineHeight, routePaint);
			canvas.drawCircle((float) this.parentWidth / 2,
					(float) (dotRadius * 1.2), dotRadius, dotFromPaint);

			cutPath.moveTo(parentWidth / 2 - dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);
			cutPath.quadTo(parentWidth / 2 - dotRadius / 2,
					(float) (this.parentHeight - dotRadius * 2.2) / 2
							- dotRadius / 2 - dotRadius / 2, parentWidth / 2,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);
			cutPath.quadTo(parentWidth / 2 + dotRadius / 2,
					(float) ((this.parentHeight + dotRadius * 1.2) / 2)
							- dotRadius / 2, parentWidth / 2 + dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);

			canvas.drawPath(cutPath, routeCut);
			break;
		case TWO_POINTS_ROUTE:
			canvas.drawRect((float) (parentWidth / 2) - lineHalfWidth,
					(float) (dotRadius * 1.2), (float) (parentWidth / 2)
							+ lineHalfWidth,
					(float) (this.parentHeight - dotRadius * 1.2), routePaint);
			canvas.drawCircle((float) this.parentWidth / 2,
					(float) (dotRadius * 1.2), dotRadius, dotFromPaint);
			canvas.drawCircle((float) this.parentWidth / 2,
					(float) (this.parentHeight - dotRadius * 1.2), dotRadius,
					dotToPaint);
			break;
		case MANY_POINTS_ROUTE:
			canvas.drawRect((float) (parentWidth / 2) - lineHalfWidth,
					(float) (dotRadius * 1.2), (float) (parentWidth / 2)
							+ lineHalfWidth, (float) (dotRadius * 1.2)
							+ lineHeight, routePaint);
			canvas.drawCircle((float) this.parentWidth / 2,
					(float) (dotRadius * 1.2), dotRadius, dotFromPaint);

			cutPath.moveTo(parentWidth / 2 - dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);
			cutPath.quadTo(parentWidth / 2 - dotRadius / 2,
					(float) (this.parentHeight - dotRadius * 2.2) / 2
							- dotRadius / 2 - dotRadius / 2, parentWidth / 2,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);
			cutPath.quadTo(parentWidth / 2 + dotRadius / 2,
					(float) ((this.parentHeight + dotRadius * 1.2) / 2)
							- dotRadius / 2, parentWidth / 2 + dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2);

			canvas.drawPath(cutPath, routeCut);

			canvas.drawRect((float) (parentWidth / 2) - lineHalfWidth,
					(float) (this.parentHeight - dotRadius * 1.2) - lineHeight,
					(float) (parentWidth / 2) + lineHalfWidth,
					(float) (this.parentHeight - dotRadius * 1.2), routePaint);

			float ky = 2 * dotRadius;
			cutPath.moveTo(parentWidth / 2 - dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2 + ky);
			cutPath.quadTo(parentWidth / 2 - dotRadius / 2,
					(float) (this.parentHeight - dotRadius * 2.2) / 2
							- dotRadius / 2 - dotRadius / 2 + ky,
					parentWidth / 2,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2 + ky);
			cutPath.quadTo(parentWidth / 2 + dotRadius / 2,
					(float) ((this.parentHeight + dotRadius * 1.2) / 2)
							- dotRadius / 2 + ky, parentWidth / 2 + dotRadius,
					(float) ((this.parentHeight - dotRadius * 1.2) / 2)
							- dotRadius / 2 + ky);

			canvas.drawPath(cutPath, routeCut);
			canvas.drawCircle((float) this.parentWidth / 2,
					(float) (this.parentHeight - dotRadius * 1.2), dotRadius,
					dotToPaint);
			break;
		}
		invalidate();
	}

	private void drawBackgound(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(Color.parseColor("#161412"));
		canvas.drawCircle((float) (this.parentWidth / 2),
				(float) (dotRadius * 1.2), (float) (dotRadius * 1.2), p);
		canvas.drawCircle((float) (this.parentWidth / 2),
				(float) (this.parentHeight - dotRadius * 1.2),
				(float) (dotRadius * 1.2), p);
		canvas.drawRect((float) (this.parentWidth / 2 - dotRadius * 1.2),
				(float) (dotRadius * 1.2),
				(float) (this.parentWidth / 2 + dotRadius * 1.2),
				(float) (this.parentHeight - dotRadius * 1.2), p);
	}

}
