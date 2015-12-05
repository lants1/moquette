package io.moquette.fce.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ReadFileUtil {

	public static byte[] readFileBytes(String path) throws IOException, URISyntaxException {
		return readFileString(path).getBytes();
	}
	
	public static String readFileString(String path) throws IOException, URISyntaxException {
		return new String(
				Files.readAllBytes(Paths.get(ReadFileUtil.class.getResource(path).toURI())),
				StandardCharsets.UTF_8);
	}
}
