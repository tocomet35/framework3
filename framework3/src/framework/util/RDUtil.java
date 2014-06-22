package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import framework.db.RecordSet;

/**
 * RD(Report Designer)를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class RDUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private RDUtil() {
	}

	/**
	 * 디폴트 열 구분자
	 */
	public static final String DEFAULT_COLSEP = "##";

	/**
	 * 디폴트 행 구분자
	 */
	public static final String DEFAULT_LINESEP = "\n";

	/**
	 * RecordSet을 RD 파일 형식으로 출력한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.render(response, rs)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 RecordSet 객체
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, RecordSet rs) {
		return render(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet을 RD 파일 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.render(response, rs)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 RecordSet 객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(lineSep);
			}
			pw.print(_rdRowStr(rs, colNms, colSep));
		}
		return rowCount;
	}

	/**
	 * RecordSet을 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) rs를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(rs)
	 * @param rs 변환할 RecordSet 객체
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(RecordSet rs) {
		return render(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet을 RD 파일 형식으로 변환한다. 
	 * <br>
	 * ex) rs를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(rs, "##", "!!")
	 * @param rs 변환할 RecordSet 객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(RecordSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(lineSep);
			}
			buffer.append(_rdRowStr(rs, colNms, colSep));
		}
		return buffer.toString();
	}

	/**
	 * ResultSet을 RD 파일 형식으로 출력한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.render(response, rs)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수 
	 */
	public static int render(HttpServletResponse response, ResultSet rs) {
		return render(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet을 RD 파일 형식으로 출력한다. 
	 * <br>
	 * ex) response로 rs를 RD 파일 형식으로 출력하는 경우 : RDUtil.render(response, rs)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RD 파일 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return 0;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table의 Field 가 소문자 인것은 대문자로 변경처리
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(lineSep);
					}
					pw.print(_rdRowStr(rs, colNms, colSep));
				}
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet을 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다. 
	 * <br>
	 * ex) rs를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(rs)
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(ResultSet rs) {
		return render(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet을 RD 파일 형식으로 변환한다. 
	 * <br>
	 * ex) rs를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(rs, "##", "!!")
	 * @param rs 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(ResultSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table의 Field 가 소문자 인것은 대문자로 변경처리
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(lineSep);
					}
					buffer.append(_rdRowStr(rs, colNms, colSep));
				}
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * Map객체를 RD 파일 형식으로 변환한다.
	 * 열 구분자로 디폴트 구분자를 사용한다. 
	 * <br>
	 * ex) map을 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(map)
	 * @param map 변환할 Map객체
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(Map<String, Object> map) {
		return render(map, DEFAULT_COLSEP);
	}

	/**
	 * Map객체를 RD 파일 형식으로 변환한다. 
	 * <br>
	 * ex) map을 열구분자 ## 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(map, "##")
	 * @param map 변환할 Map객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(Map<String, Object> map, String colSep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_rdRowStr(map, colSep));
		return buffer.toString();
	}

	/**
	 * List객체를 RD 파일 형식으로 변환한다.
	 * 행, 열 구분자로 디폴트 구분자를 사용한다.
	 * <br>
	 * ex1) mapList를 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(mapList)
	 * @param mapList 변환할 List객체
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return render(mapList, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * List객체를 RD 파일 형식으로 변환한다. 
	 * <br>
	 * ex1) mapList를 열구분자 ##, 행구분자 !! 인 RD 파일 형식으로 변환하는 경우 : String rd = RDUtil.render(mapList, "##", "!!")
	 * @param mapList 변환할 List객체
	 * @param colSep 열 구분자로 쓰일 문자열
	 * @param lineSep 행 구분자로 쓰일 문자열
	 * @return RD 파일 형식으로 변환된 문자열
	 */
	public static String render(List<Map<String, Object>> mapList, String colSep, String lineSep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			for (Map<String, Object> map : mapList) {
				buffer.append(_rdRowStr(map, colSep));
				buffer.append(lineSep);
			}
			buffer.delete(buffer.length() - lineSep.length(), buffer.length());
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 * 캐리지리턴, 라인피드 문자들을 변환하여준다.
	 * 
	 * @param str 변환할 문자열
	 */
	private static String _escapeRD(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	/**
	 * RD(리포트디자이너) 용 Row 문자열 생성
	 */
	private static String _rdRowStr(Map<String, Object> map, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				buffer.append(_escapeRD(value.toString()));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	/**
	 * RD(리포트디자이너) 용 Row 문자열 생성
	 */
	private static String _rdRowStr(RecordSet rs, String[] colNms, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (int c = 0; c < colNms.length; c++) {
			if (rs.get(colNms[c]) != null) {
				buffer.append(_escapeRD(rs.getString(colNms[c])));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	private static String _rdRowStr(ResultSet rs, String[] colNms, String colSep) {
		StringBuilder buffer = new StringBuilder();
		try {
			for (int c = 0; c < colNms.length; c++) {
				if (rs.getObject(colNms[c]) != null) {
					buffer.append(_escapeRD(rs.getString(colNms[c])));
				}
				buffer.append(colSep);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}
}
