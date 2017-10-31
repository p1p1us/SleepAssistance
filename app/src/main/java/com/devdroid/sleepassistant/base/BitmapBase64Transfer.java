package com.devdroid.sleepassistant.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapBase64Transfer {

	
	/**
	 * bitmapתΪbase64
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * base64תΪbitmap
	 * @param base64Data
	 * @return
	 */
	/*
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}*/
	public static Bitmap base64ToBitmap(String base64Data) {
		Bitmap bitmap=null; 
		try {  
			
			byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
			for (int i = 0; i < bytes.length; ++i) 
			{  
				if (bytes[i] < 0) 
				{ 
					bytes[i] += 256;  
				}  
			} 
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (Exception e) {  
			e.printStackTrace();
		}
		return bitmap;
	}

}
