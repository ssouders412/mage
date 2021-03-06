package mage.cards.p;

import mage.MageInt;
import mage.abilities.common.EntersBattlefieldControlledTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.DevotionCount;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.DamagePlayersEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.LoseCreatureTypeSourceEffect;
import mage.abilities.hint.ValueHint;
import mage.abilities.keyword.IndestructibleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.AnotherPredicate;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class PurphorosGodOfTheForge extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("another creature");

    static {
        filter.add(AnotherPredicate.instance);
    }

    private static final DynamicValue xValue = new DevotionCount(ColoredManaSymbol.R);

    public PurphorosGodOfTheForge(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT, CardType.CREATURE}, "{3}{R}");

        addSuperType(SuperType.LEGENDARY);
        this.subtype.add(SubType.GOD);

        this.power = new MageInt(6);
        this.toughness = new MageInt(5);

        // Indestructible
        this.addAbility(IndestructibleAbility.getInstance());

        // As long as your devotion to red is less than five, Purphoros isn't a creature.
        Effect effect = new LoseCreatureTypeSourceEffect(xValue, 5);
        effect.setText("As long as your devotion to red is less than five, Purphoros isn't a creature.<i>(Each {R} in the mana costs of permanents you control counts towards your devotion to red.)</i>");
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, effect).addHint(new ValueHint("Devotion to red", xValue)));

        // Whenever another creature enters the battlefield under your control, Purphoros deals 2 damage to each opponent.
        this.addAbility(new EntersBattlefieldControlledTriggeredAbility(new DamagePlayersEffect(2, TargetController.OPPONENT), filter));

        // {2}{R}: Creatures you control get +1/+0 until end of turn.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new BoostControlledEffect(1, 0, Duration.EndOfTurn), new ManaCostsImpl("{2}{R}")));
    }

    public PurphorosGodOfTheForge(final PurphorosGodOfTheForge card) {
        super(card);
    }

    @Override
    public PurphorosGodOfTheForge copy() {
        return new PurphorosGodOfTheForge(this);
    }
}
