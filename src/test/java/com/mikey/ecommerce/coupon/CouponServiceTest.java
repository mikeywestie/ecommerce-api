package com.mikey.ecommerce.coupon;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.coupon.dto.CouponResponse;
import com.mikey.ecommerce.coupon.CouponRedemptionRepository;
import com.mikey.ecommerce.coupon.dto.CreateCouponRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponRedemptionRepository couponRedemptionRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponService(
                couponRepository,
                couponRedemptionRepository
        );
    }

    @Test
    void create_shouldSaveCouponAndReturnResponse() {
        Instant expiresAt = Instant.now().plusSeconds(86_400);
        CreateCouponRequest request = new CreateCouponRequest(
                "save10",
                CouponType.PERCENTAGE,
                new BigDecimal("10.00"),
                expiresAt,
                true,
                null,
                null
        );

        when(couponRepository.existsByCodeIgnoreCase("save10")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon coupon = invocation.getArgument(0);
            setField(coupon, "id", 1L);
            return coupon;
        });

        when(couponRedemptionRepository.countByCoupon(any()))
                .thenReturn(0L);

        CouponResponse response = couponService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.code()).isEqualTo("SAVE10");
        assertThat(response.type()).isEqualTo(CouponType.PERCENTAGE);
        assertThat(response.value()).isEqualByComparingTo("10.00");
        assertThat(response.active()).isTrue();
        assertThat(response.expiresAt()).isEqualTo(expiresAt);

        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(couponCaptor.capture());

        Coupon savedCoupon = couponCaptor.getValue();
        assertThat(savedCoupon.getCode()).isEqualTo("SAVE10");
        assertThat(savedCoupon.getType()).isEqualTo(CouponType.PERCENTAGE);
        assertThat(savedCoupon.getValue()).isEqualByComparingTo("10.00");
    }

    @Test
    void create_shouldThrowApiException_whenCouponCodeAlreadyExists() {
        Instant expiresAt = Instant.now().plusSeconds(86_400);
        CreateCouponRequest request = new CreateCouponRequest(
                "SAVE10",
                CouponType.PERCENTAGE,
                new BigDecimal("10.00"),
                expiresAt,
                true,
                null,
                null
        );

        when(couponRepository.existsByCodeIgnoreCase("SAVE10")).thenReturn(true);

        assertThatThrownBy(() -> couponService.create(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Coupon code already exists");

        verify(couponRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnMappedCouponResponses() {
        Instant firstExpiry = Instant.now().plusSeconds(86_400);
        Instant secondExpiry = Instant.now().plusSeconds(172_800);

        Coupon firstCoupon = new Coupon(
                "SAVE10",
                CouponType.PERCENTAGE,
                new BigDecimal("10.00"),
                firstExpiry
        );
        Coupon secondCoupon = new Coupon(
                "LESS50",
                CouponType.FIXED_AMOUNT,
                new BigDecimal("50.00"),
                secondExpiry
        );

        setField(firstCoupon, "id", 1L);
        setField(secondCoupon, "id", 2L);
        secondCoupon.deactivate();

        when(couponRepository.findAll()).thenReturn(List.of(firstCoupon, secondCoupon));
        when(couponRedemptionRepository.countByCoupon(firstCoupon))
                .thenReturn(5L);

        when(couponRedemptionRepository.countByCoupon(secondCoupon))
                .thenReturn(2L);

        List<CouponResponse> responses = couponService.findAll();

        assertThat(responses).hasSize(2);

        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).code()).isEqualTo("SAVE10");
        assertThat(responses.get(0).type()).isEqualTo(CouponType.PERCENTAGE);
        assertThat(responses.get(0).value()).isEqualByComparingTo("10.00");
        assertThat(responses.get(0).active()).isTrue();
        assertThat(responses.get(0).expiresAt()).isEqualTo(firstExpiry);

        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).code()).isEqualTo("LESS50");
        assertThat(responses.get(1).type()).isEqualTo(CouponType.FIXED_AMOUNT);
        assertThat(responses.get(1).value()).isEqualByComparingTo("50.00");
        assertThat(responses.get(1).active()).isFalse();
        assertThat(responses.get(1).expiresAt()).isEqualTo(secondExpiry);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException(
                    "Could not set " + fieldName + " for test",
                    ex
            );
        }
    }
}