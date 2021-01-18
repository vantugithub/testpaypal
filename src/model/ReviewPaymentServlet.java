package model;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.api.payments.Transaction;

@WebServlet("/review_payment")
public class ReviewPaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ReviewPaymentServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paymentId = request.getParameter("paymentId");
		String PayerId = request.getParameter("PayerId");
		
		try {
			PaymentServices paymentServices = new PaymentServices();
			Payment payment = paymentServices.getPaymentDetails(paymentId);
			String url = "review.jsp?paymentId="+paymentId+"&PayerId="+PayerId;
			
			PayerInfo info = payment.getPayer().getPayerInfo();
			Transaction transaction = payment.getTransactions().get(0);
			ShippingAddress address  = transaction.getItemList().getShippingAddress();
			
			request.setAttribute("payer", info);
			request.setAttribute("transaction", transaction);
			request.setAttribute("shippingAddress", address);
			
			
			request.getRequestDispatcher(url).forward(request, response);
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("faildMessage", e.getMessage());
			request.getRequestDispatcher("error.jsp").forward(request, response);
		}
		
	}

}
