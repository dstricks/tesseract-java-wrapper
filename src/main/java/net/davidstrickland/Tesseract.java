package net.davidstrickland;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

/**
 * A simple Java wrapper for the Tesseract command line tool
 * <br/><br/>
 * *Important Note: This library requires a system variable called "tesseract" which is the location of the Tesseract tool (ex. -Dtesseract=/usr/bin/tesseract)
 */
public class Tesseract {
	/**
	 * OCR the image located at <code>imagePath</code> using the
	 * English language pack.
	 *
	 * @param imagePath path to the image (ex. C:/Documents/image.jpg)
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException
	 * 				if either (1) a source file could not be found <code>imagePath</code>
	 * 				or (2) the system doesn't have access to create temporary files.
	 */
	public static String process(String imagePath) throws IOException {
		return process(imagePath, null);
	}
	
	/**
	 * OCR the image located at <code>imagePath</code> using the language pack
	 * that corresponds to passed in <code>langCode</code>.
	 * 
	 * @param imagePath path to the image (ex. C:/Documents/image.jpg)
	 * @param langCode Tesseract-specific language code to use for OCR (ex. eng)
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException
	 * 				if either (1) a source file could not be found <code>imagePath</code>
	 * 				or (2) the system doesn't have access to create temporary files.
	 */
	public static String process(String imagePath, String langCode) throws IOException {
		String tesseractPath = System.getProperty("tesseract");
		
		if(tesseractPath == null) {
			throw new UnsupportedOperationException("Required 'tesseract' system variable not set");
		}
		
		String id = UUID.randomUUID().toString();
		
		List<String> args = new ArrayList<String>();
		args.add(tesseractPath);
		args.add(imagePath);
		args.add(id);

		if(langCode != null) {
			args.add("-l " + langCode);
		}

		ProcessBuilder builder = new ProcessBuilder(args);
		Process p = builder.start();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// continue on, trying to read what is available
		}
		
		// TODO: check whether the file exists or not... if not then no text was extracted??
		File result = new File(id + ".txt");
		String text = FileUtils.readFileToString(result);
		result.delete();
		
		return text;
	}
	
	/**
	 * OCR the <code>imageStream</code> using the English language pack.
	 *
	 * @param imageStream the image as an InputStream
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException if the system doesn't have access to create temporary files
	 */
	public static String process(InputStream imageStream) throws IOException {
		File tempFile = File.createTempFile("tesseract_", ".tmp");
		FileUtils.copyInputStreamToFile(imageStream, tempFile);
		
		String text = process(tempFile.getPath());
		
		tempFile.delete();

		return text;
	}
	
	/**
	 * OCR the <code>imageStream</code> using the language pack
	 * that corresponds to passed in <code>langCode</code>.
	 *
	 * @param imageStream the image as an InputStream
	 * @param langCode Tesseract-specific language code to use for OCR (ex. eng)
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException if the system doesn't have access to create temporary files
	 */
	public static String process(InputStream imageStream, String langCode) throws IOException {
		File tempFile = File.createTempFile("tesseract_", ".tmp");
		FileUtils.copyInputStreamToFile(imageStream, tempFile);
		
		String text = process(tempFile.getPath(), langCode);
		
		tempFile.delete();

		return text;
	}
	
	/**
	 * OCR the <code>bufferedImage</code> using the English language pack.
	 * 
	 * @param bufferedImage the image as a BufferedImage
	 * @param formatName name of the Image format-- ex. "jpg" (see {@link ImageIO.getReaderFormatNames();})
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException if the system doesn't have access to create temporary files
	 */
	public static String process(BufferedImage bufferedImage, String formatName) throws IOException {
		String text = "";

		File temp = File.createTempFile("tesseract_", ".tmp");
		ImageIO.write(bufferedImage, formatName, temp);
		
		text = process(temp.getAbsolutePath());
		temp.delete();
		
		return text;
	}
	
	/**
	 * OCR the <code>bufferedImage</code> using  the language pack
	 * that corresponds to passed in <code>langCode</code>.
	 * 
	 * @param bufferedImage the image as a BufferedImage
	 * @param formatName name of the Image format-- ex. "jpg" (see {@link ImageIO.getReaderFormatNames();})
	 * @param langCode Tesseract-specific language code to use for OCR (ex. eng)
	 * @return Any text that was extracted, or empty String if no text was extracted
	 * @throws IOException if the system doesn't have access to create temporary files
	 */
	public static String process(BufferedImage bufferedImage, String formatName, String langCode) throws IOException {
		String text = "";

		File temp = File.createTempFile("tesseract_", ".tmp");
		ImageIO.write(bufferedImage, formatName, temp);
		
		text = process(temp.getAbsolutePath(), langCode);
		temp.delete();
		
		return text;
	}
}
