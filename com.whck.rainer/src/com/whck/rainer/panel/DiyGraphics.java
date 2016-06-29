package com.whck.rainer.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;

public class DiyGraphics {

	
	/**依照图片的不规则图形*/
	public static void createIrregularlyShell(Composite parent, Image image) {

		Region region = new Region();
		ImageData imageData = image.getImageData();
		if (imageData.alphaData != null) {
			for (int y = 0; y < imageData.height; y++) {
				for (int x = 0; x < imageData.width; x++) {
					if (imageData.getAlpha(x, y) == 255) {
						region.add(imageData.x + x, imageData.y + y, 1, 1);
					}
				}
			}
		} else {
			ImageData mask = imageData.getTransparencyMask();
			for (int y = 0; y < mask.height; y++) {
				for (int x = 0; x < mask.width; x++) {
					if (mask.getPixel(x, y) != 0) {
						region.add(imageData.x + x, imageData.y + y, 1, 1);
					}
				}
			}
		}
		parent.setRegion(region);
		parent.setSize(imageData.x + imageData.width, imageData.y + imageData.height);
	}

	/**圆环*/
	public static void createAnnularShell(int centerX,int centerY,int outer_radius, int inner_radius, Composite parent) {
		parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		Region region = new Region();
		region.add(createCircle(outer_radius, centerX, centerY));
		region.subtract(createCircle(inner_radius, centerX, centerY));
		parent.setRegion(region);
		Rectangle regionBounds = region.getBounds();
		parent.setSize(regionBounds.width, regionBounds.height);
	}

	/**圆形*/
	protected static int[] createCircle(int radius, int centerX, int centerY) {
		int[] points = new int[720 * 2];
		for (int i = 0; i < 720; i++) {
			int theX = centerX + (int) (radius * Math.cos(Math.toRadians(i / 2)));
			int theY = centerY + (int) (radius * Math.sin(Math.toRadians(i / 2)));
			points[i * 2] = theX;
			points[i * 2 + 1] = theY;
		}
		return points;
	}
	/**圆角矩形*/
	public static int[] arc_rect(int offsetX, int offsetY, int radius, int width, int height) {
		int[] arc_rect = new int[8 * radius + 8];
		int left_margin = offsetX - width / 2;
		int top_margin = offsetY - height / 2;
		for (int i = 0; i <= radius; i++) {
			int x = left_margin + i;
			int deta_y = (int) Math.sqrt(radius * radius - (radius - i) * (radius - i));
			int y = radius - deta_y + top_margin;
			// 左上
			arc_rect[i * 2] = x;
			arc_rect[i * 2 + 1] = y;
			// 右下
			arc_rect[2 * i + 4 * radius + 4] = 2 * offsetX - x;
			arc_rect[2 * i + 4 * radius + 5] = 2 * offsetY - y;
		}
		for (int i = 0; i <= radius; i++) {
			int x = width + left_margin - radius + i;
			int deta_y = (int) Math.sqrt(radius * radius - i * i);
			int y = top_margin + radius - deta_y;
			// 右上
			arc_rect[2 * i + 2 * radius + 2] = x;
			arc_rect[2 * i + 2 * radius + 3] = y;
			// 左下
			arc_rect[2 * i + 6 * radius + 6] = 2 * offsetX - x;
			arc_rect[2 * i + 6 * radius + 7] = 2 * offsetY - y;
		}
		return arc_rect;
	}

}
