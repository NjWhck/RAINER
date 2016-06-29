package com.whck.rainer.panel;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.FrameworkUtil;

import com.whck.rainer.ZoneListView;

public class PictureFactory {
	private static Map<ImageDescriptor, Image> imageCache = new HashMap<>(28);
	private static  Map<ColorDescriptor, Color> colorCache = new HashMap<>(18);

	public PictureFactory() {}

	public static Image getImage(String iconName) {
		ImageDescriptor descriptor = null;
		if (iconName.equals("风向")) {
			descriptor = getImageDescriptor("风向.bmp");
		} else if (iconName.equals("风速")) {
			descriptor = getImageDescriptor("风速.bmp");
		} else if (iconName.equals("流量")) {
			descriptor = getImageDescriptor("流量.bmp");
		} else if (iconName.equals("压强")) {
			descriptor = getImageDescriptor("压强.bmp");
		} else if (iconName.equals("雨强")) {
			descriptor = getImageDescriptor("雨强.bmp");
		} else if (iconName.equals("雨型")) {
			descriptor = getImageDescriptor("雨型.bmp");
		} else if (iconName.equals("雨量")) {
			descriptor = getImageDescriptor("雨量.bmp");
		}  else if (iconName.equals("variable")) {
			descriptor = getImageDescriptor("variable.png");
		} else if (iconName.equals("dev")) {
			descriptor = getImageDescriptor("dev.png");
		} else if (iconName.equals("false")) {
			descriptor = getImageDescriptor("false.bmp");
		} else if (iconName.equals("true")) {
			descriptor = getImageDescriptor("true.bmp");
		}else if (iconName.equals("countdown")) {
			descriptor = getImageDescriptor("countdown.bmp");
		} else if (iconName.equals("device_online")) {
			descriptor = getImageDescriptor("device_online.png");
		}else if (iconName.equals("device_offline")) {
			descriptor = getImageDescriptor("device_offline.png");
		}else if (iconName.equals("zone")) {
			descriptor = getImageDescriptor("zone.png");
		}else if (iconName.equals("variable_ctrl")) {
			descriptor = getImageDescriptor("variable_ctrl.png");
		}else if (iconName.equals("variable_show")) {
			descriptor = getImageDescriptor("variable_show.png");
		}else if (iconName.equals("device_panel_bg")) {
			descriptor = getImageDescriptor("device_panel_bg.bmp");
		}else if (iconName.equals("wind_motor_bg")) {
			descriptor = getImageDescriptor("wind_motor_bg.gif");
		}else if (iconName.equals("device_bg")) {
			descriptor = getImageDescriptor("device_bg.png");
		}else if (iconName.equals("db_temp")) {
			descriptor = getImageDescriptor("db_temp.png");
		}else if (iconName.equals("db_gif")) {
			descriptor = getImageDescriptor("db_gif.png");
		}else if (iconName.equals("db_semi")) {
			descriptor = getImageDescriptor("db_semi.png");
		}else if (iconName.equals("db_round")) {
			descriptor = getImageDescriptor("db_round.png");
		}else if (iconName.equals("semi_pointer")) {
			descriptor = getImageDescriptor("semi_pointer.png");
		}else if (iconName.equals("round_pointer")) {
			descriptor = getImageDescriptor("round_pointer.png");
		}else if (iconName.equals("termRain")) {
			descriptor = getImageDescriptor("termRain.png");
		}else if (iconName.equals("dblRain")) {
			descriptor = getImageDescriptor("dblRain.png");
		}else if (iconName.equals("trpRain")) {
			descriptor = getImageDescriptor("trpRain.png");
		}else if (iconName.equals("altrRain")) {
			descriptor = getImageDescriptor("altrRain.png");
		}else {
			throw unknownElement(iconName);
		}
		Image image = (Image) imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}
	public static Color getColor(String  colorName) {
		ColorDescriptor descriptor = null;
		descriptor = getColorDescriptor(colorName);
		Color color = (Color) colorCache.get(descriptor);
		if (color == null) {
			color = descriptor.createColor(null);
			colorCache.put(descriptor, color);
		}
		return color;
	}
	
	protected static RuntimeException unknownElement(String element) {
		return new RuntimeException("Unknown type of element  " + element);
	}
	private static  ColorDescriptor getColorDescriptor(String colorName) {
		if (colorName.equals("浅红")) {
			return ColorDescriptor.createFrom(new RGB(205,92,92));
		} else if (colorName.equals("蓝色")) {
			return ColorDescriptor.createFrom(new RGB(0,0,255));
		} else if (colorName.equals("红色")) {
			return ColorDescriptor.createFrom(new RGB(192,00,00));
		} else if (colorName.equals("绿色")) {
			return ColorDescriptor.createFrom(new RGB(0,255,0));
		} else if (colorName.equals("灰色")) {
			return ColorDescriptor.createFrom(new RGB(70,00,70));
		} else if (colorName.equals("红棕")) {
			return ColorDescriptor.createFrom(new RGB(184,112,112));
		}else if (colorName.equals("橙色")) {
			return ColorDescriptor.createFrom(new RGB(255,165,0));
		}else if (colorName.equals("黑色")) {
			return ColorDescriptor.createFrom(new RGB(0,0,0));
		} else if (colorName.equals("浅灰")) {
			return ColorDescriptor.createFrom(new RGB(129,129,129));
		}else if (colorName.equals("浅橙")) {
			return ColorDescriptor.createFrom(new RGB(249,192,143));
		}else {
			throw unknownElement(colorName);
		}
	}
	private  static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "images/";
		URL url = FrameworkUtil.getBundle(ZoneListView.class).getResource(iconPath + name);
		return ImageDescriptor.createFromURL(url);
	}
	public static  void dispose(){
		for (Iterator<Image> it = imageCache.values().iterator(); it.hasNext();) {
			it.next().dispose();
		}
		for (Iterator<Color> it = colorCache.values().iterator(); it.hasNext();) {
			it.next().dispose();
		}
		imageCache.clear();
		colorCache.clear();
	}
}
