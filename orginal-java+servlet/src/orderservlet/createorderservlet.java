package orderservlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sql.goodsql;
import sql.ordersql;
import sqlimpl.goodsqlimpl;
import sqlimpl.ordersqlimpl;
import vo.good;
import vo.order;
import vo.user;

/**
 * Servlet implementation class findallorderservlet
 */
public class createorderservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createorderservlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");	
		HttpSession session = request.getSession();
		user u = (user)session.getAttribute("admin");
		String  address ;
		String  telephone  ;
		String  buyername  ;
		if(null==u) {
			address = request.getParameter("address");
	 		telephone = request.getParameter("telephone");
	 		buyername = request.getParameter("buyername");
		}
		else {
			address=u.getAddress();
			telephone=u.getPhone();
			buyername=u.getUsername();
		}
	 	int goodid = Integer.parseInt(request.getParameter("goodid"));
	 	int number = Integer.parseInt(request.getParameter("number"));
	 	System.out.println("order = "+ number);
        int orderstate = 1;//状态零：未成功订单
        
        goodsql gs=new goodsqlimpl();
        good g=null;
        try {
        	g=new good();
			g=gs.search(goodid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        int owner=g.getOwner();//要的是卖家id而不是买家
        if(null!= g&&g.getNumber()>=number) {
        	int tmp=g.getNumber();
        	//设置商品库存
        	g.setNumber(tmp-number);
        	//如果剩余库存等于下单库存，就设为冻结
        	if(tmp==number) {
    				g.setState(1);
        	}
        	try {
        		
				gs.updateGood(g);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        ordersql ors = new ordersqlimpl();
	        order orf = null;
	        System.out.println("order1 = "+ number);
	        try {
	        	System.out.println("enter");
				orf=new order();
				orf.setAddress(address);
				orf.setTelephone(telephone);
				orf.setBuyername(buyername);
				orf.setGoodid(goodid);
				orf.setNumber(number);
				orf.setOrderstate(orderstate);
				orf.setOwner(owner);
				ors.add(orf);
				
				 List<order> orList = null;
				 try {
						orList = ors.showall(u.getUserid());
				 } catch (SQLException e) {
					e.printStackTrace();
				 }
					
				 request.setAttribute("orL", orList);
				 request.setAttribute("message","成功创建订单");
				request.setAttribute("to","buyermain");
				request.getRequestDispatcher("success.jsp").forward(request,response); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else {
		    request.setAttribute("err","请输入正确的商品id");
		    request.setAttribute("to","createorder");
		    request.getRequestDispatcher("error.jsp").forward(request,response);
		}
		
	}

}
