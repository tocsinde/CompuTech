/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.model;
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
}
	