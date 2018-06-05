/**
 * tree-node - Implementation of tree structures in java
 * Copyright © 2018 Edwin Njeru (mailnjeru@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.ghacupha.tree_node;

import io.github.ghacupha.tree_node.util.MultipleParentsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("linkedNode tests using MultipleParentsException")
public class MoreLinkedNodeTests {

    static TreeNode<Account> accounts = new LinkedMultiTreeNode<Account>(new Account("Accounts"));
    static TreeNode<Account> assetAccount = new LinkedMultiTreeNode<Account>(new Account("Asset Account"));
    static TreeNode<Account> currentAccount = new LinkedMultiTreeNode<Account>(new Account("Current Account"));
    static TreeNode<Account> nonCurrentAccount = new LinkedMultiTreeNode<Account>(new Account("Non Current Account"));
    static TreeNode<Account> cashAccount = new LinkedMultiTreeNode<Account>(new Account("Cash Account"));
    static TreeNode<Account> savingsAccount = new LinkedMultiTreeNode<Account>(new Account("Savings Account"));
    static TreeNode<Account> treasuryBond = new LinkedMultiTreeNode<Account>(new Account("T-Bond"));
    static TreeNode<Account> mpesaAccount = new LinkedMultiTreeNode<Account>(new Account("Mpesa Account"));
    static TreeNode<Account> termDeposit = new LinkedMultiTreeNode<Account>(new Account("Term Deposit"));
    static TreeNode<Account> sharesAccount = new LinkedMultiTreeNode<Account>(new Account("Shares Account"));
    static TreeNode<Account> taxRecoverable = new LinkedMultiTreeNode<Account>(new Account("Tax Recoverable"));
    static TreeNode<Account> sundryDebtors = new LinkedMultiTreeNode<Account>(new Account("Sundry Debtors"));
    static TreeNode<Account> fixedAssets = new LinkedMultiTreeNode<Account>(new Account("Fixed Assets"));
    static TreeNode<Account> furnitureAndFittings = new LinkedMultiTreeNode<Account>(new Account("Furniture And Fittings"));
    static TreeNode<Account> electronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Electronic Equipment"));
    static TreeNode<Account> financeDeptElectronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Finance Dept Electronic Equipment"));
    static TreeNode<Account> creditDeptElectronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Credit Dept Electronic Equipment"));
    static TreeNode<Account> marketingDeptElectronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Marketing Dept Electronic Equipment"));
    static TreeNode<Account> ceoDeptElectronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Ceo Dept Electronic Equipment"));
    static TreeNode<Account> payablesDeptElectronicEquipment = new LinkedMultiTreeNode<Account>(new Account("Payables Dept Electronic Equipment"));
    static TreeNode<Account> computers = new LinkedMultiTreeNode<Account>(new Account("Computers"));
    static TreeNode<Account> financeDeptComputers = new LinkedMultiTreeNode<Account>(new Account("Finance Dept Computers"));
    static TreeNode<Account> creditDeptComputers = new LinkedMultiTreeNode<Account>(new Account("Credit Dept Computers"));
    static TreeNode<Account> marketingDeptComputers = new LinkedMultiTreeNode<Account>(new Account("Marketing Dept Computers"));
    static TreeNode<Account> ceoDeptComputers = new LinkedMultiTreeNode<Account>(new Account("Ceo Dept Computers"));
    static TreeNode<Account> payablesDeptComputers = new LinkedMultiTreeNode<Account>(new Account("Payables Dept Computers"));
    static TreeNode<Account> landAndBuildings = new LinkedMultiTreeNode<Account>(new Account("Land and Buildings"));
    static TreeNode<Account> officeRenovations = new LinkedMultiTreeNode<Account>(new Account("Office Renovations"));
    static TreeNode<Account> motorVehicles = new LinkedMultiTreeNode<Account>(new Account("Motor Vehicles"));
    static TreeNode<Account> ceoDeptMotorVehicles = new LinkedMultiTreeNode<Account>(new Account("Ceo Dept Motor Vehicles"));
    static TreeNode<Account> gmdDeptMotorVehicles = new LinkedMultiTreeNode<Account>(new Account("Gmd Dept Motor Vehicles"));
    static TreeNode<Account> financeDeptMotorVehicles = new LinkedMultiTreeNode<Account>(new Account("Finance Dept Motor Vehicles"));
    static TreeNode<Account> computerSoftware = new LinkedMultiTreeNode<Account>(new Account("Computer Software"));
    static TreeNode<Account> investment1 = new LinkedMultiTreeNode<Account>(new Account("Investment1"));
    static TreeNode<Account> investment2 = new LinkedMultiTreeNode<Account>(new Account("Investment2"));
    static TreeNode<Account> investment3 = new LinkedMultiTreeNode<Account>(new Account("Investment3"));
    static TreeNode<Account> investment4 = new LinkedMultiTreeNode<Account>(new Account("Investment4"));
    static TreeNode<Account> liabilityAccount = new LinkedMultiTreeNode<Account>(new Account("Liability Account"));
    static TreeNode<Account> sundryCreditors = new LinkedMultiTreeNode<Account>(new Account("Sundry Creditors"));

    @BeforeEach
    public void setUp(){
        accounts.add(assetAccount);

        // Add major asset categories
        assetAccount.add(currentAccount);
        assetAccount.add(nonCurrentAccount);

        // Add major current account categories
        currentAccount.add(cashAccount);
        currentAccount.add(savingsAccount);
        currentAccount.add(treasuryBond);
        currentAccount.add(mpesaAccount);

        // Add major non-current account categories
        nonCurrentAccount.add(termDeposit);
        nonCurrentAccount.add(sharesAccount);
        nonCurrentAccount.add(taxRecoverable);
        nonCurrentAccount.add(sundryDebtors);
        nonCurrentAccount.add(fixedAssets);

        // Add types of fixed assets account
        fixedAssets.add(furnitureAndFittings);
        fixedAssets.add(electronicEquipment);
        fixedAssets.add(computers);
        fixedAssets.add(landAndBuildings);
        fixedAssets.add(officeRenovations);
        fixedAssets.add(motorVehicles);
        fixedAssets.add(computerSoftware);

        // Add shares account
        sharesAccount.add(investment1);
        sharesAccount.add(investment2);
        sharesAccount.add(investment3);
        sharesAccount.add(investment4);

        // Add another major type of account
        accounts.add(liabilityAccount);

        // Add major tyep of liability account
        liabilityAccount.add(sundryCreditors);

        // Add service outlet hierarchy to computers account
        computers.add(financeDeptComputers);
        computers.add(creditDeptComputers);
        computers.add(marketingDeptComputers);
        computers.add(ceoDeptComputers);
        computers.add(payablesDeptComputers);

        // Add service outlet hierarchy to electronic equipment account
        electronicEquipment.add(financeDeptElectronicEquipment);
        electronicEquipment.add(creditDeptElectronicEquipment);
        electronicEquipment.add(marketingDeptElectronicEquipment);
        electronicEquipment.add(ceoDeptElectronicEquipment);
        electronicEquipment.add(payablesDeptElectronicEquipment);

        // Add service outlet hierarchy to motor vehicles account
        motorVehicles.add(financeDeptMotorVehicles);
        motorVehicles.add(gmdDeptMotorVehicles);
        motorVehicles.add(ceoDeptMotorVehicles);
    }

    @Test
    @DisplayName("Test that MulitpleParentsException is thrown")
    public void willThrowMultipleParentsException(){

        assertThrows(MultipleParentsException.class, () -> {
            motorVehicles.add(financeDeptComputers);
        });
    }
}
