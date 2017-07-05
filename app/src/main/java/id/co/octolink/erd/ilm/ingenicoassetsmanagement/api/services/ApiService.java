package id.co.octolink.erd.ilm.ingenicoassetsmanagement.api.services;

import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.CustomerResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.HistoryResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.Inventory;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.LoginResponse;
import id.co.octolink.erd.ilm.ingenicoassetsmanagement.model.StaffResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by ILM on 6/29/2016.
 */
public interface ApiService {
    //-----------------------------------Phase 0.1--------------------------------------------------

    //Login
    @FormUrlEncoded
    @POST("Staff/auth")
    Call<LoginResponse> staff_auth_login(@Field("staff_code") String staffCode,
                                         @Field("password") String password);

    //IN tanpa Customer
    @FormUrlEncoded
    @POST("Inventory/checkIn")
    Call<Inventory> stockInNoCstmr(@Field("staff_code") String WHStaff,
                                   @Field("staff_code_in") String FSStaff,
                                   @Field("brand_code_new") String brandCode,
                                   @Field("serial_number_new") String serialNumber);

    //IN dengan Customer
    @FormUrlEncoded
    @POST("Inventory/checkIn")
    Call<Inventory> stockInWithCstmr(@Field("staff_code") String staffCode,
                                     @Field("staff_code_in") String FSStaff,
                                     @Field("brand_code_new") String brandCode,
                                     @Field("serial_number_new") String serialNumber,
                                     @Field("customer_code") String customerCode);

    //OUT
    @FormUrlEncoded
    @POST("Inventory/checkOut")
    Call<Inventory> stockOut(@Field("staff_code") String staffCode,
                             @Field("staff_code_out") String FSStaff,
                             @Field("brand_code_new") String brandCode,
                             @Field("serial_number_new") String serialNumber);

    //FIELD
    @FormUrlEncoded
    @POST("Inventory/checkField")
    Call<Inventory> field(@Field("staff_code") String staffCode,
                          @Field("brand_code_new") String brandCode,
                          @Field("serial_number_new") String serialNumber);

    //History IN
    @FormUrlEncoded
    @POST("Inventory/historyIn")
    Call<HistoryResponse> historyStockIn(@Field("staff_code") String staffCode);

    //History OUT
    @FormUrlEncoded
    @POST("Inventory/historyOut")
    Call<HistoryResponse> historyStockOut(@Field("staff_code") String staffCode);

    //History FIELD
    @FormUrlEncoded
    @POST("Inventory/historyField")
    Call<HistoryResponse> historyField(@Field("staff_code") String staffCode);

    //Customer
    @GET("customer/all")
    Call<CustomerResponse> getAllCustomer();

    //Checker
    @FormUrlEncoded
    @POST("inventoryorder/checkStatus")
    Call<Inventory> checkSerialNumber(@Field("brand_code_new") String brandCode,
                                      @Field("serial_number_new") String serialNumber);

    //GET - All Staff
    @GET("staff/all")
    Call<StaffResponse> getAllStaff();

}
