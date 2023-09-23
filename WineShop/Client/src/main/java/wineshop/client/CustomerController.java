package wineshop.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import wineshop.model.Customer;
import wineshop.model.GlobalVarAndUtilities;
import wineshop.model.Order;
import wineshop.model.Wine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Controller Class for managing the Customer view
 * @author Mirko Piazza, Camilla Franceschini
 */
public class CustomerController {
    /**
     * Client for server connection
     */
    private Client client;
    /**
     * Customer logged
     */
    private Customer c;
    /**
     * Wine to manage
     */
    private Wine w;
    /**
     * Order to manage
     */
    private Order o;
    /**
     * Order's states visible from a Customer
     */
    private String[] choice = {GlobalVarAndUtilities.orderStates.Completato.toString(), GlobalVarAndUtilities.orderStates.Richiesto.toString(), GlobalVarAndUtilities.orderStates.ConfermaUtente.toString(), GlobalVarAndUtilities.orderStates.Annullato.toString()};
    /**
     * Token for avoid to manage orders multiple times
     */
    private boolean tokenOrder = false;
    /**
     * Token to handle the payment methods choices
     */
    private boolean tokenPMWine = false;
    /**
     * Array to handle the views switch by AnchorPane's list
     */
    private ArrayList<AnchorPane> anchorPanes;

    /**
     * Zone to declare all javafx objects
     */
    @FXML
    private AnchorPane apHome;
    @FXML
    private AnchorPane apWine;
    @FXML
    private AnchorPane apWineDetails;
    @FXML
    private AnchorPane apOrder;
    @FXML
    private AnchorPane apOrderDetails;
    @FXML
    private TableView wineTV;
    @FXML
    private TextField nameField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField wineName;
    @FXML
    private TextField wineProducer;
    @FXML
    private TextField wineOrigin;
    @FXML
    private TextField wineYear;
    @FXML
    private TextField wineVines;
    @FXML
    private TextArea wineNotes;
    @FXML
    private TextField quantityBuy;
    @FXML
    private TextField priceWine;
    @FXML
    private ComboBox paymentMethodCombo;
    @FXML
    private TableView orderTV;
    @FXML
    private ComboBox stateCombo;
    @FXML
    private DatePicker startDateField;
    @FXML
    private DatePicker endDateField;
    @FXML
    private TextField orderId;
    @FXML
    private TextField orderWine;
    @FXML
    private TextField orderQty;
    @FXML
    private TextField orderPrice;
    @FXML
    private TextField orderDate;
    @FXML
    private TextField orderPropDate;
    @FXML
    private TextField reviewValue;

    /**
     * Calculate the final price applying discount based on bottles purchased
     * @param price The price of a single bottle
     * @param quantity The quantity of bottles purchased
     * @return The total price after the discount
     */
    private Double PriceAfterDiscount(Double price, int quantity) {
        Double finalPrice = price*quantity;
        if(quantity < 6) {
            return finalPrice;
        } else if (quantity == 6) {
            return finalPrice-(finalPrice*5/100);
        } else if (quantity == 12) {
            return finalPrice-(finalPrice*10/100);
        } else if (quantity == 18) {
            return finalPrice-(finalPrice*10/100)-(finalPrice*2/100);
        } else {
            return finalPrice-(finalPrice*10/100)-(finalPrice*3/100);
        }
    }

    /**
     * Set the view to show
     * @param ap The AnchorPane to show
     */
    private void setVisibility(AnchorPane ap)
    {
        anchorPanes = new ArrayList<AnchorPane>(Arrays.asList(apHome, apWine, apOrder, apWineDetails, apOrderDetails));
        ap.setVisible(true);
        for (int i = 0; i < anchorPanes.size(); i++){
            if(ap.getId() != anchorPanes.get(i).getId()) {
                anchorPanes.get(i).setVisible(false);
            }
        }
    }

    /**
     * Set the TableViews
     * @param view The view for manage which fields to show in the TableView
     * @param tv The TableView to populate
     * @param list The list of objects for populate the TableView
     */
    private void setTableView(String view, TableView tv, ArrayList<?> list)
    {
        ArrayList<String> values = new ArrayList<>();
        tv.getColumns().clear();
        tv.getItems().clear();
        TableColumn<Wine, String> tc;
        if(view == "CustomerWineSearch") {
            values.add(0, "name");
            values.add(1, "producer");
            values.add(2, "year");
            values.add(3, "salePrice");
        } else if (view == "CustomerOrderSearch") {
            if (Objects.equals(stateCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.Completato.toString())) {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "orderDate");
                values.add(5, "deliveryDate");
                values.add(6, "reviewEmployee");
            } else if (Objects.equals(stateCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.ConfermaUtente.toString()) || Objects.equals(stateCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.Annullato.toString())) {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "orderDate");
                values.add(5, "proposalDelivery");
            } else {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "orderDate");
            }
        }
        for (int i = 0 ; i < values.size() ; i++) {
            tc = new TableColumn<>(values.get(i));
            tc.setCellValueFactory(new PropertyValueFactory<>(values.get(i)));
            double width = (int) (tv.getWidth() / values.size());
            tc.setPrefWidth(width);
            tv.getColumns().add(i,tc);
        }
        for (Object o : list) {
            tv.getItems().add(o);
        }
    }

    /**
     * Lock the calendars for avoid to insert text manually
     */
    @FXML
    protected void LockCalendar()
    {
        startDateField.getEditor().setEditable(false);
        endDateField.getEditor().setEditable(false);
    }

    /**
     * Show Home's view
     */
    @FXML
    protected void onHomeClick() {
        setVisibility(apHome);
    }

    /**
     * Show Wines's view and wines list (eventually filtered)
     */
    @FXML
    protected void onWineSearchClick() {
        setVisibility(apWine);
        client = new Client();
        c = (Customer) ViewHandler.u;
        ArrayList<Wine> winelist;
        String name = nameField.getText();
        String year = yearField.getText();
        if(GlobalVarAndUtilities.isNumeric(year) || year.isEmpty()) {
            winelist = client.listWines(name, year);
        } else {
            client.clientPopup("Enter a year", true);
            winelist = client.listWines(name, "");
        }
        setTableView("CustomerWineSearch", wineTV, winelist);
        client.closeConnection();
    }

    /**
     * Highlight a Wine to give the possibility to buy it
     */
    @FXML
    protected void onWineTVClick() {
        w = (Wine) wineTV.getSelectionModel().getSelectedItem();
        if(w != null) {
            setVisibility(apWineDetails);
            wineName.setText(w.getName());
            wineProducer.setText(w.getProducer());
            wineOrigin.setText(w.getOrigin());
            wineYear.setText(String.valueOf(w.getYear()));
            wineVines.setText(w.getVines());
            wineNotes.setText(w.getTechnicalNotes());
            quantityBuy.setText(String.valueOf(1));
            priceWine.setText(String.valueOf(w.getSalePrice()));
            if(!tokenPMWine) {
                String [] choice = {"Credit Card", "Wire Transfer"};
                paymentMethodCombo.getItems().addAll(choice);
                tokenPMWine = true;
            }
            priceWine.setText(String.valueOf(w.getSalePrice()));
        }
    }

    /**
     * Send a new Order based on the values of the input fields
     */
    @FXML
    protected void onBuyButtonClick() {
        client = new Client();
        if(paymentMethodCombo.getSelectionModel().getSelectedItem() != null) {
            if(client.insertCustomerOrder(w, c, Integer.valueOf(quantityBuy.getText()), Double.valueOf(priceWine.getText()))) {
                client.clientPopup("Purchase request entered", false);
                client.closeConnection();
                setVisibility(apWine);
            } else {
                client.clientPopup("Request failed, please try again later", true);
            }
        } else {
            client.clientPopup("Select a payment method", true);
        }
    }

    /**
     * Decreases the quantity of Wine to buy
     */
    @FXML
    protected void onLessBuyButtonClick() {
        if(Integer.valueOf(quantityBuy.getText()) >= 12) {
            quantityBuy.setText(String.valueOf(Integer.valueOf(quantityBuy.getText()) - 6));
        } else if (Integer.valueOf(quantityBuy.getText()) > 1) {
            quantityBuy.setText(String.valueOf(Integer.valueOf(quantityBuy.getText()) - 1));
        } else { return; }
        priceWine.setText(String.valueOf(PriceAfterDiscount(w.getSalePrice(),Integer.valueOf(quantityBuy.getText()))));
    }

    /**
     * Increases the quantity of Wine to buy
     */
    @FXML
    protected void onMoreBuyButtonClick() {
        if(Integer.valueOf(quantityBuy.getText()) >= 6) {
            quantityBuy.setText(String.valueOf(Integer.valueOf(quantityBuy.getText()) + 6));
        } else {
            quantityBuy.setText(String.valueOf(Integer.valueOf(quantityBuy.getText()) + 1));
        }
        priceWine.setText(String.valueOf(PriceAfterDiscount(w.getSalePrice(), Integer.valueOf(quantityBuy.getText()))));
    }

    /**
     * Show My Orders's view and orders list (eventually filtered)
     */
    @FXML
    protected void onOrderSearchClick() {
        setVisibility(apOrder);
        LockCalendar();
        client = new Client();
        c = (Customer) ViewHandler.u;
        ArrayList<Order> orderlist;
        if(stateCombo.getItems().size() == 0) {
            stateCombo.getItems().addAll(choice);
            stateCombo.setValue(choice[0]);
        }
        orderlist = client.listOrders(stateCombo.getSelectionModel().getSelectedItem().toString(), startDateField.getEditor().getText(), endDateField.getEditor().getText(), c);
        setTableView("CustomerOrderSearch", orderTV, orderlist);
        client.closeConnection();
    }

    /**
     * Highlight an Order to give the possibility to accept or refuse it
     */
    @FXML
    protected void onOrderTVClick() {
        if(tokenOrder) {
            onOrderSearchClick();
            tokenOrder = false;
            return;
        }
        o = (Order) orderTV.getSelectionModel().getSelectedItem();
        //A Customer can manage only an Order in its confirmation waiting
        if(o != null && Objects.equals(o.getState(), GlobalVarAndUtilities.orderStates.ConfermaUtente.toString())) {
            setVisibility(apOrderDetails);
            orderId.setText(String.valueOf(o.getIdOrder()));
            orderWine.setText(o.getWineName());
            orderQty.setText(String.valueOf(o.getQuantity()));
            orderPrice.setText(String.valueOf(o.getPrice()));
            orderDate.setText(String.valueOf(o.getOrderDate()));
            orderPropDate.setText(String.valueOf(o.getProposalDelivery()));
            tokenOrder = true;
        }
    }

    /**
     * Accept the Order highlighted
     */
    @FXML
    protected void onConfirmOrderButtonClick() {
        client = new Client();
        if(client.responseOrderCustomer(o, GlobalVarAndUtilities.orderStates.Completato.toString(), Integer.valueOf(reviewValue.getText()))) {
            client.clientPopup("Order completed", false);
            client.closeConnection();
            setVisibility(apOrder);
        } else {
            client.clientPopup("Request failed, please try again later", true);
        }
    }

    /**
     * Cancel the Order highlighted
     */
    @FXML
    protected void onRejectOrderButtonClick() {
        client = new Client();
        if(client.responseOrderCustomer(o, GlobalVarAndUtilities.orderStates.Annullato.toString(), Integer.valueOf(reviewValue.getText()))) {
            client.clientPopup("Order cancelled", false);
            client.closeConnection();
            setVisibility(apOrder);
        } else {
            client.clientPopup("Request failed, please try again later", true);
        }
    }

    /**
     * Decreases the review of the Employee who handled the Order
     */
    @FXML
    protected void onDecreaseReviewButtonClick() {
        if (Integer.valueOf(reviewValue.getText()) > 1) {
            reviewValue.setText(String.valueOf(Integer.valueOf(reviewValue.getText()) - 1));
        } else { return; }
    }

    /**
     * Increases the review of the Employee who handled the Order
     */
    @FXML
    protected void onIncreaseReviewButtonClick() {
        if (Integer.valueOf(reviewValue.getText()) < 5) {
            reviewValue.setText(String.valueOf(Integer.valueOf(reviewValue.getText()) + 1));
        } else { return; }
    }
}