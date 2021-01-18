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
import com.paypal.base.rest.PayPalRESTException;
@WebServlet("/execute_payment")
public class ExecutePaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ExecutePaymentServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paymentId = request.getParameter("paymentId");
		String PayerId = request.getParameter("PayerID");
		try {
			PaymentServices paymentServices = new PaymentServices();
			Payment payment = paymentServices.executePayment(paymentId, PayerId);
			
			
			PayerInfo info = payment.getPayer().getPayerInfo();
			Transaction transaction = payment.getTransactions().get(0);
			ShippingAddress address  = transaction.getItemList().getShippingAddress();
			
			request.setAttribute("payer", info);
			request.setAttribute("transaction", transaction);
			request.setAttribute("shippingAddress", address);
			
			request.getRequestDispatcher("receipt.jsp").forward(request, response);
			
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			request.setAttribute("faildMessage", e.getMessage());
			request.getRequestDispatcher("error.jsp").forward(request, response);
		}
		
		
	}

}
