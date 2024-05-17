package com.vgen.billreader.services;


import com.vgen.billreader.model.TotalBillForMonth;
import com.vgen.billreader.repositrory.TotalBillForMonthRepositrory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class TotalBillForMonthServicesTest {
    @Mock
    private  TotalBillForMonthRepositrory totalBillForMonthRepositrory;
    //TotalBillForMonthRepositrory totalBillForMonthRepositrory= Mockito.mock(TotalBillForMonthRepositrory.class);
    TotalBillForMonthServices totalBillForMonthServices;
    TotalBillForMonth totalBillForMonth;
    @BeforeEach
    void setUp() {
         totalBillForMonthServices=new TotalBillForMonthServices(totalBillForMonthRepositrory);

        totalBillForMonth=new TotalBillForMonth();
        totalBillForMonth.setMonth(5);
        totalBillForMonth.setMobileNumber("201-702-3929");
        totalBillForMonth.setBillAmount(12.00);
        totalBillForMonth.setYear(2024);
        totalBillForMonth.setName("Varun");
        totalBillForMonth.setId(1L);


    }
    @Test
    @DisplayName("test To save data")
    void testSaveData() {

    }

    @Test
    @DisplayName("Test Find by ID ")
    void testFindById() {
        setUp();
       Mockito.when(totalBillForMonthServices.findById(1L)).thenReturn(Optional.of(totalBillForMonth));

        TotalBillForMonth responseTotalBillForMonth=null;
        var data=totalBillForMonthRepositrory.findById(1L);
        if(data.isPresent()) responseTotalBillForMonth = data.get();
        Assertions.assertThat(responseTotalBillForMonth).isNotNull();
        Assertions.assertThat(responseTotalBillForMonth.getMonth()).isEqualTo(5);
    }

    @Test
    @DisplayName("Test Find by Month And Year")
    void testFindbyMonthAndYear() {



    }
}