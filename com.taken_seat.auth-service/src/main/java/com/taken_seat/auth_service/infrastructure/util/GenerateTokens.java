package com.taken_seat.auth_service.infrastructure.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateTokens {

	private static final String LOGIN_URL = "http://localhost:19091/api/v1/auths/login";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws IOException {

		log.info("[Auth] 토큰 발급 - 시작");

		BufferedWriter writer = new BufferedWriter(new FileWriter("tokens.csv"));
		writer.write("email,token\n");

		for (int i = 1; i <= 1000; i++) {
			String email = "user" + i + "@example.com";
			String password = "password" + i + "!";
			String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email,
				password);

			System.out.println("요청 바디: " + requestBody);

			try {
				log.info("[Auth] 토큰 발급 요청 - 시작 - email={}", email);

				URL url = new URL(LOGIN_URL);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setDoOutput(true);
				conn.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

				InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
				Scanner scanner = new Scanner(stream).useDelimiter("\\A");

				String responseBody = scanner.hasNext() ? scanner.next() : "";

				JsonNode jsonNode = objectMapper.readTree(responseBody);
				JsonNode tokenNode = jsonNode.get("body");
				if (tokenNode != null && tokenNode.get("accessToken") != null) {
					String token = tokenNode.get("accessToken").asText();
					writer.write(String.format("%s,%s\n", email, token));
					log.info("[Auth] 토큰 발급 - 성공 - email={}", email);
				} else {
					log.error("[Auth] 응답 형식 이상 - email={}, 응답={}", email, responseBody);
				}
			} catch (Exception e) {
				log.error("[Auth] 토큰 발급 - 실패 - email={}, 이유={}", email, e.toString(), e);
			}
		}

		writer.close();
		log.info("[Auth] tokens.csv 생성 완료");
	}
}