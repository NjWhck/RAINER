package com.whck.msf.panel;

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

import com.whck.msf.ZoneListView;

public class PictureFactory {
	private static Map<ImageDescriptor, Image> imageCache = new HashMap<>(20);
	private static  Map<ColorDescriptor, Color> colorCache = new HashMap<>(20);

	public PictureFactory() {}

	public static Image getImage(String iconName) {
		ImageDescriptor descriptor = null;
		if (iconName.equals("device_online")) {
			descriptor = getImageDescriptor("device_online.png");
		}else if (iconName.equals("device_offline")) {
			descriptor = getImageDescriptor("device_offline.png");
		}else if (iconName.equals("zone")) {
			descriptor = getImageDescriptor("zone.png");
		}else if (iconName.equals("variable_ctrl")) {
			descriptor = getImageDescriptor("variable_ctrl.png");
		}else if (iconName.equals("variable_show")) {
			descriptor = getImageDescriptor("variable_show.png");
		}else if (iconName.equals("orientation_65")) {
			descriptor = getImageDescriptor("orientation_65.gif");
		}else if (iconName.equals("orientation_100")) {
			descriptor = getImageDescriptor("orientation_100.gif");
		}else if (iconName.equals("orientation_120")) {
			descriptor = getImageDescriptor("orientation_120.gif");
		}else if (iconName.equals("db_temp_65")) {
			descriptor = getImageDescriptor("db_temp_65.png");
		}else if (iconName.equals("db_temp_100")) {
			descriptor = getImageDescriptor("db_temp_100.png");
		}else if (iconName.equals("db_temp_120")) {
			descriptor = getImageDescriptor("db_temp_120.png");
		}else if (iconName.equals("db_semi_65")) {
			descriptor = getImageDescriptor("db_semi_65.png");
		}else if (iconName.equals("db_semi_100")) {
			descriptor = getImageDescriptor("db_semi_100.png");
		}else if (iconName.equals("db_semi_120")) {
			descriptor = getImageDescriptor("db_semi_120.png");
		}else if (iconName.equals("semi_pointer_65")) {
			descriptor = getImageDescriptor("semi_pointer_65.png");
		}else if (iconName.equals("semi_pointer_100")) {
			descriptor = getImageDescriptor("semi_pointer_100.png");
		}else if (iconName.equals("semi_pointer_120")) {
			descriptor = getImageDescriptor("semi_pointer_120.png");
		}else if (iconName.equals("db_round_65")) {
			descriptor = getImageDescriptor("db_round_65.png");
		}else if (iconName.equals("db_round_100")) {
			descriptor = getImageDescriptor("db_round_100.png");
		}else if (iconName.equals("db_round_120")) {
			descriptor = getImageDescriptor("db_round_120.png");
		}else if (iconName.equals("round_pointer_65")) {
			descriptor = getImageDescriptor("round_pointer_65.png");
		}else if (iconName.equals("round_pointer_100")) {
			descriptor = getImageDescriptor("round_pointer_100.png");
		}else if (iconName.equals("round_pointer_120")) {
			descriptor = getImageDescriptor("round_pointer_120.png");
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
		if (colorName.equals("Pink")) {
			return ColorDescriptor.createFrom(new RGB(255,121,188));
		} else if (colorName.equals("Blue")) {
			return ColorDescriptor.createFrom(new RGB(0,0,227));
		}else if (colorName.equals("LigthBlue")) {
			return ColorDescriptor.createFrom(new RGB(40,40,255));
		} else if (colorName.equals("Red")) {
			return ColorDescriptor.createFrom(new RGB(192,00,00));
		}else if (colorName.equals("LightRed")) {
			return ColorDescriptor.createFrom(new RGB(255,117,117));
		}else if (colorName.equals("kermesinus")) {
			return ColorDescriptor.createFrom(new RGB(116,58,58));
		} else if (colorName.equals("Green")) {
			return ColorDescriptor.createFrom(new RGB(0,236,0));
		} else if (colorName.equals("Gray")) {
			return ColorDescriptor.createFrom(new RGB(81,81,81));
		} else if (colorName.equals("Brown")) {
			return ColorDescriptor.createFrom(new RGB(255,88,9));
		}else if (colorName.equals("Orange")) {
			return ColorDescriptor.createFrom(new RGB(255,128,0));
		}else if (colorName.equals("Black")) {
			return ColorDescriptor.createFrom(new RGB(0,0,0));
		} else if (colorName.equals("LightGray")) {
			return ColorDescriptor.createFrom(new RGB(136,136,136));
		}else if (colorName.equals("Violet")) {
			return ColorDescriptor.createFrom(new RGB(143,69,134));
		}else if (colorName.equals("Lavender")) {
			return ColorDescriptor.createFrom(new RGB(183,102,173));
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
