package model;

import java.util.ArrayList;
import java.util.List;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

public class PaymentServices {
	
	private static final String CLIENT_ID = "ARK44EmTZWFcrF3HZs9bWHs7xznHgjDAGvHVlyTrw6_zvFd6240wkQKpdPytPW4BkwiQrBNfoyIS4JMz";
	private static final String CLIENT_SECRET = "EOSYNlom-mqp6rd-n_1W_bZxWpl55rO929bXMjPjSTCtiIelMg_zHAID7Krr8cGr3K8tncoftqzSrlm4";
	private static final String MODE = "sandbox";
	
	public String authorizePayment(OrderDetail orderDetail) throws PayPalRESTException {
		Payer payer = getPayerInformation();
		RedirectUrls redirectUrls = getRedirectUrls();
		List<Transaction> listTransaction = getTransactionInformation(orderDetail);
		
		Payment payment = new Payment();
		payment.setTransactions(listTransaction).setRedirectUrls(redirectUrls).setPayer(payer).setIntent("authorize");
		
		APIContext apiContext = new APIContext( CLIENT_ID,CLIENT_SECRET, MODE);
		Payment approvedPayment = payment.create(apiContext);
		System.out.println(approvedPayment);
		return getApprovalLink(approvedPayment);
	}
	
	private String getApprovalLink(Payment approvedPayment) {
		List<Links> links  = approvedPayment.getLinks();
		String ApprovalLink = null;
		for(Links link : links) {
			if(link.getRel().equalsIgnoreCase("approval_url")) {
				ApprovalLink = link.getHref();
			}
		}
		return ApprovalLink;
	}
	
	private List<Transaction> getTransactionInformation(OrderDetail detail){
		Details details = new Details();
		details.setShipping(detail.getShipping());
		details.setSubtotal(detail.getSubtotal());
		details.setTax(detail.getTax());
		
		Amount amount  = new Amount();
		amount.setCurrency("USD");
		amount.setDetails(details);
		amount.setTotal(detail.getTotal());
		
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDescription(detail.getProductName());
		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<Item>();
		
		
		Item item = new Item();
		item.setCurrency("USD")
		.setName(detail.getProductName())
		.setPrice(detail.getSubtotal())
		.setTax(detail.getTax())
		.setQuantity("1");
		items.add(item);
		itemList.setItems(items);
		
		transaction.setItemList(itemList);
		List<Transaction> listTransaction = new ArrayList<Transaction>();
		listTransaction.add(transaction);
		
		
		return listTransaction;
	}

	private RedirectUrls getRedirectUrls() {
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:8080/TestPayPal/cancel.html");
		redirectUrls.setReturnUrl("http://localhost:8080/TestPayPal/review_payment");
		return redirectUrls;
	}

	private Payer getPayerInformation() {
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		
		PayerInfo info = new PayerInfo();
		info.setFirstName("Tu").setLastName("Nguyen").setEmail("nguyenvantu11041999@gmail.com");
		payer.setPayerInfo(info);
		
		return payer;
	}
	
	public Payment executePayment(String paymentId,String payerId) throws PayPalRESTException {
		PaymentExecution execution = new PaymentExecution();
		execution.setPayerId(payerId);
		
		Payment payment= new Payment().setId(paymentId);
		APIContext apiContext = new APIContext(CLIENT_ID,CLIENT_SECRET, MODE);
		
		return payment.execute(apiContext, execution);
		
	}
	
	
	
	public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(CLIENT_ID,CLIENT_SECRET, MODE);
		return Payment.get(apiContext, paymentId);
	}
}
