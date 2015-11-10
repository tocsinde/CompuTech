package kickstart.model;
import java.math.RoundingMode;

import javax.persistence.Entity;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
@Entity
public class SaleOrder extends Order {
	public boolean isSale = false;
	
	 public SaleOrder() {
	 
	 }
   public SaleOrder(UserAccount userAccount, 	PaymentMethod paymentMethod)
   { 
	   super(userAccount, paymentMethod);
   }
   
   
   
   public Money getTotalPrice() {
	   Money money = super.getTotalPrice();
	   if (isSale)
		   money.multipliedBy(BossController.sale, RoundingMode.UNNECESSARY);
	   return money;
   }
}
	