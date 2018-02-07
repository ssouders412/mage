/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.e;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.keyword.AscendEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.designations.DesignationType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetNonlandPermanent;

/**
 *
 * @author LevelX2
 */
public class ExpelFromOrazca extends CardImpl {

    public ExpelFromOrazca(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{1}{U}");

        // Ascend
        this.getSpellAbility().addEffect(new AscendEffect());

        // Return target nonland permanent to its owner's hand. If you have the city's blessing, you may put that permanent on top of its owner's library instead.
        this.getSpellAbility().addEffect(new ExpelFromOrazcaEffect());
        this.getSpellAbility().addTarget(new TargetNonlandPermanent());
    }

    public ExpelFromOrazca(final ExpelFromOrazca card) {
        super(card);
    }

    @Override
    public ExpelFromOrazca copy() {
        return new ExpelFromOrazca(this);
    }
}

class ExpelFromOrazcaEffect extends OneShotEffect {

    public ExpelFromOrazcaEffect() {
        super(Outcome.ReturnToHand);
        this.staticText = "Return target nonland permanent to its owner's hand. If you have the city's blessing, you may put that permanent on top of its owner's library instead";
    }

    public ExpelFromOrazcaEffect(final ExpelFromOrazcaEffect effect) {
        super(effect);
    }

    @Override
    public ExpelFromOrazcaEffect copy() {
        return new ExpelFromOrazcaEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Permanent targetPermanent = game.getPermanent(getTargetPointer().getFirst(game, source));
            if (targetPermanent != null) {
                if (controller.hasDesignation(DesignationType.CITYS_BLESSING)
                        && controller.chooseUse(outcome, "Put " + targetPermanent.getIdName() + " on top of its owner's library instead?", source, game)) {
                    controller.moveCards(targetPermanent, Zone.LIBRARY, source, game);
                } else {
                    controller.moveCards(targetPermanent, Zone.HAND, source, game);
                }

            }
            return true;
        }
        return false;
    }
}