package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.db.RecordSet;

/**
 * jqGrid 를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class JQGridUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.JQGridUtil.class);

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private JQGridUtil() {
	}

	/**
	 * RecordSet을 jqGrid 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 jqGrid 형식으로 출력하는 경우 : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs jqGrid 형식으로 변환할 RecordSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + _jqGridRowStr(rs, colNms));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet을 jqGrid 형식으로 출력한다. 
	 * <br>
	 * ex) response로 rs를 jqGrid 형식으로 출력하는 경우 : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs jqGrid 형식으로 변환할 RecordSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @param colNames 컬럼이름 배열
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + _jqGridRowStr(rs, colNames));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet을 jqGrid 형식으로 변환한다. 
	 * <br>
	 * ex) rs를 jqGrid 형식으로 변환하는 경우 : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * @param rs jqGrid 형식으로 변환할 RecordSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @return jqGrid 형식으로 변환된 문자열
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage) {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + _jqGridRowStr(rs, colNms));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * RecordSet을 jqGrid 형식으로 변환한다. 
	 * <br>
	 * ex) rs를 jqGrid 형식으로 변환하는 경우 : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param rs jqGrid 형식으로 변환할 RecordSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @param colNames 컬럼이름 배열
	 * @return jqGrid 형식으로 변환된 문자열
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + _jqGridRowStr(rs, colNames));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * ResultSet을 jqGrid 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 jqGrid 형식으로 출력하는 경우 : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs jqGrid 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		try {
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				pw.print("{");
				int rowCount = 0;
				pw.print("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print("{");
					pw.print("\"id\":" + rowCount + ",");
					pw.print("\"cell\":" + _jqGridRowStr(rs, colNms));
					pw.print("}");
				}
				pw.print("],");
				pw.print("\"total\":" + totalPage + ",");
				pw.print("\"page\":" + currentPage + ",");
				pw.print("\"records\":" + totalCount);
				pw.print("}");
				return rowCount;
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * ResultSet을 jqGrid 형식으로 출력한다. 
	 * <br>
	 * ex) response로 rs를 jqGrid 형식으로 출력하는 경우 : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs jqGrid 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @param colNames 컬럼이름 배열
	 * @return 처리건수
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		try {
			PrintWriter pw = response.getWriter();
			try {
				pw.print("{");
				int rowCount = 0;
				pw.print("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print("{");
					pw.print("\"id\":" + rowCount + ",");
					pw.print("\"cell\":" + _jqGridRowStr(rs, colNames));
					pw.print("}");
				}
				pw.print("],");
				pw.print("\"total\":" + totalPage + ",");
				pw.print("\"page\":" + currentPage + ",");
				pw.print("\"records\":" + totalCount);
				pw.print("}");
				return rowCount;
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * ResultSet을 jqGrid 형식으로 변환한다.
	 * <br>
	 * ex) rs를 jqGrid 형식으로 변환하는 경우 : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * @param rs jqGrid 형식으로 변환할 ResultSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @return jqGrid 형식으로 변환된 문자열
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append("{");
					buffer.append("\"id\":" + rowCount + ",");
					buffer.append("\"cell\":" + _jqGridRowStr(rs, colNms));
					buffer.append("}");
				}
				buffer.append("],");
				buffer.append("\"total\":" + totalPage + ",");
				buffer.append("\"page\":" + currentPage + ",");
				buffer.append("\"records\":" + totalCount);
				buffer.append("}");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * ResultSet을 jqGrid 형식으로 변환한다. 
	 * <br>
	 * ex) rs를 jqGrid 형식으로 변환하는 경우 : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param rs jqGrid 형식으로 변환할 ResultSet 객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @param colNames 컬럼이름 배열
	 * @return jqGrid 형식으로 변환된 문자열
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append("{");
					buffer.append("\"id\":" + rowCount + ",");
					buffer.append("\"cell\":" + _jqGridRowStr(rs, colNames));
					buffer.append("}");
				}
				buffer.append("],");
				buffer.append("\"total\":" + totalPage + ",");
				buffer.append("\"page\":" + currentPage + ",");
				buffer.append("\"records\":" + totalCount);
				buffer.append("}");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * List객체를 jqGrid 형식으로 변환한다. 
	 * <br>
	 * ex1) mapList를 jqGrid 형식으로 변환하는 경우 : String json = JQGridUtil.render(mapList, totalCount, currentPage, rowsPerPage)
	 * @param mapList 변환할 List객체
	 * @param totalCount 전체페이지수
	 * @param currentPage 현재페이지수
	 * @param rowsPerPage 한페이지에 표시할 로우수
	 * @return jqGrid 형식으로 변환된 문자열
	 */
	public static String render(List<Map<String, Object>> mapList, int totalCount, int currentPage, int rowsPerPage) {
		if (mapList == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		buffer.append("{");
		buffer.append("\"rows\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				rowCount++;
				buffer.append("{");
				buffer.append("\"id\":" + rowCount + ",");
				buffer.append("\"cell\":" + _jqGridRowStr(map));
				buffer.append("}");
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("],");
		} else {
			buffer.append("[],");
		}
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 유틸리티

	/**
	 * 자바스크립트상에 특수하게 인식되는 문자들을 JSON등에 사용하기 위해 변환하여준다.
	 * @param str 변환할 문자열
	 */
	public static String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 * jqGrid 용 Row 문자열 생성
	 */
	private static String _jqGridRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("[");
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	/**
	 * jqGrid 용 Row 문자열 생성
	 */
	private static String _jqGridRowStr(RecordSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		if (colNms != null && colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	private static String _jqGridRowStr(ResultSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value;
				try {
					value = rs.getObject(colNms[c].toUpperCase());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}
}
