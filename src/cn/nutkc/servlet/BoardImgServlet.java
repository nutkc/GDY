package cn.nutkc.servlet;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class BoardImgServlet extends HttpServlet {
	private static Image pbg = null;
	private static Image pbg_ = null;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("img/png;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		ServletOutputStream out = response.getOutputStream();
		/**
		 * 此Servlet用来给浏览器返回用户名牌图片资源
		 */
		// 1.检查请求的浏览器是否在user_room表存在
		ServletContext sc = request.getServletContext();
		String id = request.getRequestedSessionId();
		Map<String,String> user_room = (Map<String,String>) sc.getAttribute("user_room");
		String room = user_room.get(id);
		if(this.pbg == null) {
			this.pbg = ImageIO.read(request.getServletContext().getResource("/WEB-INF/imgs/player.png"));
		}
		if(this.pbg_ == null) {
			this.pbg_ = ImageIO.read(request.getServletContext().getResource("/WEB-INF/imgs/player_.png"));
		}
		String nickname = request.getParameter("nickname");
		nickname = URLDecoder.decode(nickname, "utf-8");
		nickname = new String(nickname.getBytes("ISO-8859-1"),"utf-8");
		String isproduce = request.getParameter("isproduce");
		createImg(nickname, out, isproduce.equals("true"));
		out.close();
	}
	
	private void createImg(String name, OutputStream out, boolean isPro) throws IOException {
		BufferedImage bi = new BufferedImage(135, 56, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = bi.getGraphics();
		g.setFont(new Font("ft", Font.BOLD, 30));
		if(isPro) {
			g.drawImage(pbg_, 0, 0, null);
		} else {
			g.drawImage(pbg, 0, 0, null);
		}
		this.drawString(g,name, 0, 43);
		ImageIO.write(bi, "png", out);
	}
	public void drawString(Graphics g, String str, int xPos, int yPos) {
        int strWidth = g.getFontMetrics().stringWidth(str);
        g.drawString(str, xPos + (135-strWidth) / 2, yPos);
	}

}
