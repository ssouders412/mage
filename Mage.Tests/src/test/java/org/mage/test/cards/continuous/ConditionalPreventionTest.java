package org.mage.test.cards.continuous;

import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.common.MyTurnCondition;
import mage.abilities.condition.common.NotMyTurnCondition;
import mage.abilities.decorator.ConditionalPreventionEffect;
import mage.abilities.effects.common.PreventAllDamageToAllEffect;
import mage.abilities.effects.common.PreventAllDamageToPlayersEffect;
import mage.constants.Duration;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author JayDi85
 */
public class ConditionalPreventionTest extends CardTestPlayerBase {

    // conditional effects go to layered, but there are prevention effects list too

    @Test
    public void test_NotPreventDamage() {
        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 0);
        assertHandCount(playerA, "Lightning Bolt", 0);
    }

    @Test
    public void test_PreventDamageNormal() {
        addCustomCardWithAbility("prevent", playerA, new SimpleStaticAbility(new PreventAllDamageToAllEffect(Duration.WhileOnBattlefield, StaticFilters.FILTER_PERMANENT)));

        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 1);
        assertHandCount(playerA, "Lightning Bolt", 0);
    }

    @Test
    public void test_PreventDamageConditionalActive() {
        addCustomCardWithAbility("prevent", playerA, new SimpleStaticAbility(
                new ConditionalPreventionEffect(
                        new PreventAllDamageToAllEffect(Duration.WhileOnBattlefield, StaticFilters.FILTER_PERMANENT),
                        MyTurnCondition.instance,
                        ""
                )
        ));

        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 1);
        assertHandCount(playerA, "Lightning Bolt", 0);
    }

    @Test
    public void test_PreventDamageConditionalNotActive() {
        addCustomCardWithAbility("prevent", playerA, new SimpleStaticAbility(
                new ConditionalPreventionEffect(
                        new PreventAllDamageToAllEffect(Duration.WhileOnBattlefield, StaticFilters.FILTER_PERMANENT),
                        NotMyTurnCondition.instance,
                        ""
                )
        ));

        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 1);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 0);
        assertHandCount(playerA, "Lightning Bolt", 0);
    }

    @Test
    public void test_PreventDamageConditionalNotActiveWithOtherEffect() {
        addCustomCardWithAbility("prevent", playerA, new SimpleStaticAbility(
                new ConditionalPreventionEffect(
                        new PreventAllDamageToAllEffect(Duration.WhileOnBattlefield, StaticFilters.FILTER_PERMANENT),
                        new PreventAllDamageToPlayersEffect(Duration.WhileOnBattlefield, false),
                        NotMyTurnCondition.instance,
                        ""
                )
        ));

        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 2);
        addCard(Zone.HAND, playerA, "Lightning Bolt", 2);

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", "Balduvian Bears"); // will prevent
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Lightning Bolt", playerA); // will not prevent

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 0); // not prevented, dies
        assertLife(playerA, 20); // prevented, no damage
        assertHandCount(playerA, "Lightning Bolt", 0);
    }

    @Test
    public void test_PrentableCombatDamage() {
        // Prevent all damage that would be dealt to creatures.
        addCard(Zone.BATTLEFIELD, playerA, "Bubble Matrix", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        //
        addCard(Zone.BATTLEFIELD, playerB, "Balduvian Bears", 1);

        // player A must do damage
        attack(1, playerA, "Balduvian Bears", playerB);

        // player B can't do damage (bears must block and safe)
        attack(4, playerB, "Balduvian Bears", playerA);
        block(4, playerA, "Balduvian Bears", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(4, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 1);
        assertPermanentCount(playerB, "Balduvian Bears", 1);
        assertLife(playerA, 20);
        assertLife(playerB, 20 - 2);
    }

    @Test
    public void test_UnpreventableCombatDamage() {
        // Combat damage that would be dealt by creatures you control can't be prevented.
        addCard(Zone.BATTLEFIELD, playerB, "Questing Beast", 1);
        //
        // Prevent all damage that would be dealt to creatures.
        addCard(Zone.BATTLEFIELD, playerA, "Bubble Matrix", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Balduvian Bears", 1);
        //
        addCard(Zone.BATTLEFIELD, playerB, "Balduvian Bears", 1);

        // player A must do damage
        attack(1, playerA, "Balduvian Bears", playerB);

        // player B must be prevented by Bubble Matrix, but can't (Questing Beast)
        // a -> b -- can't do damage (matrix)
        // b -> a -- can do damage (matrix -> quest)
        attack(4, playerB, "Balduvian Bears", playerA);
        block(4, playerA, "Balduvian Bears", "Balduvian Bears");

        setStrictChooseMode(true);
        setStopAt(4, PhaseStep.END_TURN);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerA, "Balduvian Bears", 0);
        assertPermanentCount(playerB, "Balduvian Bears", 1);
        assertLife(playerA, 20);
        assertLife(playerB, 20 - 2);
    }
}
