/*
 * Copyright (c) 2019 - Vicente Cartas Espinel (vicente.cartas at outlook.com). All Rights Reserved.
 * Copyright (C) 2020-2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 *
 * MechWarrior Copyright Microsoft Corporation. MekHQ was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */
package mekhq.campaign.finances;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.joda.money.BigMoney;

/**
 * This class represents a quantity of money and its associated currency.
 *
 * @author Vicente Cartas Espinel (vicente.cartas at outlook.com)
 */
public class Money implements Comparable<Money> {
    private final BigMoney wrapped;

    private Money(BigMoney money) {
        Objects.requireNonNull(money);
        this.wrapped = money;
    }

    private BigMoney getWrapped() {
        return wrapped;
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigMoney.of(currency.getCurrencyUnit(), amount));
    }

    public static Money of(double amount) {
        return Money.of(amount, CurrencyManager.getInstance().getDefaultCurrency());
    }

    public static Money zero(Currency currency) {
        return new Money(BigMoney.zero(currency.getCurrencyUnit()));
    }

    public static Money zero() {
        return zero(CurrencyManager.getInstance().getDefaultCurrency());
    }

    public boolean isZero() {
        return getWrapped().isZero();
    }

    public boolean isPositive() {
        return getWrapped().isPositive();
    }

    public boolean isPositiveOrZero() {
        return getWrapped().isPositive() || getWrapped().isZero();
    }

    public boolean isNegative() {
        return getWrapped().isNegative();
    }

    public boolean isGreaterThan(Money other) {
        return getWrapped().isGreaterThan(other.getWrapped());
    }

    public boolean isGreaterOrEqualThan(Money other) {
        return getWrapped().isGreaterThan(other.getWrapped()) || getWrapped().isEqual(other.getWrapped());
    }

    public boolean isLessThan(Money other) {
        return getWrapped().isLessThan(other.getWrapped());
    }

    public BigDecimal getAmount() {
        return getWrapped().getAmount();
    }

    public Money absolute() {
        return isPositiveOrZero() ? this : this.multipliedBy(-1);
    }

    public Money plus(Money amount) {
        if (amount == null) {
            return plus(0L);
        }

        return new Money(getWrapped().plus(amount.getWrapped()));
    }

    public Money plus(double amount) {
        return new Money(getWrapped().plus(amount));
    }

    public Money plus(List<Money> amounts) {
        return new Money(getWrapped().plus((Iterable<BigMoney>) (amounts.stream().map(Money::getWrapped)::iterator)));
    }

    public Money minus(Money amount) {
        if (amount == null) {
            return minus(0L);
        }

        return new Money(getWrapped().minus(amount.getWrapped()));
    }

    public Money minus(long amount) {
        return new Money(getWrapped().minus(amount));
    }

    public Money minus(double amount) {
        return new Money(getWrapped().minus(amount));
    }

    public Money minus(List<Money> amounts) {
        return new Money(getWrapped().minus((Iterable<BigMoney>) (amounts.stream().map(Money::getWrapped)::iterator)));
    }

    public Money multipliedBy(long amount) {
        return new Money(getWrapped().multipliedBy(amount));
    }

    public Money multipliedBy(double amount) {
        return new Money(getWrapped().multipliedBy(amount));
    }

    public Money dividedBy(double amount) {
        return new Money(getWrapped().dividedBy(amount, RoundingMode.HALF_EVEN));
    }

    public Money dividedBy(Money money) {
        return new Money(getWrapped().dividedBy(money.getWrapped().getAmount(), RoundingMode.HALF_EVEN));
    }

    public String toAmountString() {
        return CurrencyManager.getInstance().getUiAmountPrinter().print(getWrapped().toMoney(RoundingMode.HALF_EVEN));
    }

    public String toAmountAndSymbolString() {
        return CurrencyManager.getInstance()
                     .getUiAmountAndSymbolPrinter()
                     .print(getWrapped().toMoney(RoundingMode.HALF_EVEN));
    }

    /**
     * @return a new money object, rounded to use a scale of 0 with no trailing 0's
     */
    public Money round() {
        return new Money(getWrapped().withScale(0, RoundingMode.HALF_UP));
    }

    // region File I/O
    public String toXmlString() {
        return CurrencyManager.getInstance().getXmlMoneyFormatter().print(getWrapped().toMoney(RoundingMode.HALF_EVEN));
    }

    public static Money fromXmlString(String xmlData) {
        return new Money(CurrencyManager.getInstance().getXmlMoneyFormatter().parseBigMoney(xmlData));
    }
    // endregion File I/O

    @Override
    public String toString() {
        return getWrapped().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Money) && getWrapped().isEqual(((Money) obj).getWrapped());
    }

    @Override
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public int compareTo(Money o) {
        return (o != null) ? getWrapped().compareTo(o.getWrapped()) : -1;
    }
}
