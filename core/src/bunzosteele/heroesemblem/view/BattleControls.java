package bunzosteele.heroesemblem.view;

import java.io.IOException;
import java.util.ArrayList;

import bunzosteele.heroesemblem.HeroesEmblem;
import bunzosteele.heroesemblem.model.BattleState;
import bunzosteele.heroesemblem.model.Units.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class BattleControls
{
	HeroesEmblem game;
	BattleState state;
	int idleFrame = 1;
	int attackFrame = 1;
	int xOffset;
	int yOffset;
	int largeButtonWidth;
	int smallButtonWidth;
	int height;
	Sprite inactiveButton;
	Sprite activeButton;
	Sprite emphasisButton;
	Sprite button;

	public BattleControls(final HeroesEmblem game, final BattleState state, final int height, final int endOffset, final int endWidth)
	{
		this.game = game;
		this.state = state;
		Timer.schedule(new Task()
		{
			@Override
			public void run()
			{
				BattleControls.this.idleFrame++;
				BattleControls.this.attackFrame++;
				if (BattleControls.this.idleFrame > 3)
				{
					BattleControls.this.idleFrame = 1;
				}
				if (BattleControls.this.attackFrame > 2)
				{
					BattleControls.this.attackFrame = 1;
				}
			}
		}, 0, 1 / 3.0f);
		this.xOffset = 0;
		this.yOffset = 0;
		this.largeButtonWidth = endOffset / 3;
		this.smallButtonWidth = endWidth;
		this.height = height;
		final AtlasRegion inactiveRegion = this.game.textureAtlas.findRegion("InactiveButton");
		this.inactiveButton = new Sprite(inactiveRegion);
		final AtlasRegion activeRegion = this.game.textureAtlas.findRegion("ActiveButton");
		this.activeButton = new Sprite(activeRegion);
		final AtlasRegion emphasisRegion = this.game.textureAtlas.findRegion("EmphasisButton");
		this.emphasisButton = new Sprite(emphasisRegion);
		final AtlasRegion buttonRegion = this.game.textureAtlas.findRegion("Button");
		this.button = new Sprite(buttonRegion);
	}

	public void draw()
	{
		this.game.font.getData().setScale(.25f);
		this.drawMove();
		this.drawAttack();
		this.drawAbility();
		this.drawEnd();
		this.game.font.getData().setScale(.33f);
	}

	private void drawAbility()
	{
		this.game.font.setColor(Color.WHITE);
		if ((this.state.selected != null) && (this.state.selected.ability != null))
		{
			this.game.font.draw(this.game.batcher, this.state.selected.ability.displayName, this.xOffset + (2 * this.largeButtonWidth), this.yOffset + ((3 * this.height) / 5), this.largeButtonWidth, 1, false);
		} else
		{
			this.game.font.draw(this.game.batcher, "Ability", this.xOffset + (2 * this.largeButtonWidth), this.yOffset + ((3 * this.height) / 5), this.largeButtonWidth, 1, false);
		}
	}

	private void drawAbilityBackground()
	{
		if (this.state.CanUseAbility(this.state.selected))
		{
			if (this.state.isUsingAbility)
			{
				this.game.batcher.draw(emphasisButton, this.xOffset + (2 * this.largeButtonWidth), this.yOffset, this.largeButtonWidth, this.height);
			}else{
				this.game.batcher.draw(activeButton, this.xOffset + (2 * this.largeButtonWidth), this.yOffset, this.largeButtonWidth, this.height);
			}
		}else{
			this.game.batcher.draw(inactiveButton, this.xOffset + (2 * this.largeButtonWidth), this.yOffset, this.largeButtonWidth, this.height);
		}
	}

	private void drawAttack()
	{
		this.game.font.setColor(Color.WHITE);
		this.game.font.draw(this.game.batcher, "Attack", this.xOffset + this.largeButtonWidth, this.yOffset + ((3 * this.height) / 5), this.largeButtonWidth, 1, false);
	}

	private void drawAttackBackground()
	{
		if (this.state.CanAttack(this.state.selected))
		{
			if (this.state.isAttacking)
			{
				this.game.batcher.draw(emphasisButton, this.xOffset + this.largeButtonWidth, this.yOffset, this.largeButtonWidth, this.height);
			}else{
				this.game.batcher.draw(activeButton, this.xOffset + this.largeButtonWidth, this.yOffset, this.largeButtonWidth, this.height);
			}
		}else{
			this.game.batcher.draw(inactiveButton, this.xOffset + this.largeButtonWidth, this.yOffset, this.largeButtonWidth, this.height);
		}
	}

	public void drawBackground()
	{
		this.drawMoveBackground();
		this.drawAttackBackground();
		this.drawAbilityBackground();
		this.drawEndBackground();
	}

	private void drawEnd()
	{
		this.game.font.setColor(Color.WHITE);
		this.game.font.draw(this.game.batcher, "End Turn", this.xOffset + (3 * this.largeButtonWidth), this.yOffset + ((3 * this.height) / 5), this.smallButtonWidth, 1, false);
	}

	private void drawEndBackground()
	{
		if (!this.hasActions())
		{
			this.game.batcher.draw(emphasisButton, this.xOffset + (3 * this.largeButtonWidth), this.yOffset, this.smallButtonWidth, this.height);
		}else{
			this.game.batcher.draw(button, this.xOffset + (3 * this.largeButtonWidth), this.yOffset, this.smallButtonWidth, this.height);
		}
	}

	private void drawMove()
	{
		this.game.font.setColor(Color.WHITE);
		this.game.font.draw(this.game.batcher, "Move", this.xOffset, this.yOffset + ((3 * this.height) / 5), this.largeButtonWidth, 1, false);
	}

	private void drawMoveBackground()
	{
		if (this.state.CanMove())
		{
			if (this.state.isMoving)
			{
				this.game.batcher.draw(emphasisButton, this.xOffset, this.yOffset, this.largeButtonWidth, this.height);
			}else{
				this.game.batcher.draw(activeButton, this.xOffset, this.yOffset, this.largeButtonWidth, this.height);
			}
		}else{
			this.game.batcher.draw(inactiveButton, this.xOffset, this.yOffset, this.largeButtonWidth, this.height);
		}
	}

	private boolean hasActions()
	{
		for (final Unit unit : this.state.roster)
		{
			if (!this.state.IsTapped(unit))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isTouched(final float x, final float y)
	{
		if ((x >= this.xOffset) && (x < (this.xOffset + (3 * this.largeButtonWidth) + this.smallButtonWidth)))
		{
			if ((y >= this.yOffset) && (y < (this.yOffset + this.height)))
			{
				return true;
			}
		}
		return false;
	}

	public void processAbilityTouch()
	{
		this.state.isMoving = false;
		this.state.isAttacking = false;
		if (this.state.CanUseAbility(this.state.selected))
		{
			this.state.isUsingAbility = !this.state.isUsingAbility;
			if (!this.state.selected.ability.areTargetsPersistent)
			{
				this.state.selected.ability.targets = new ArrayList<Unit>();
			}
		}
	}

	public void processAttackTouch()
	{
		this.state.isMoving = false;
		this.state.isUsingAbility = false;
		if (this.state.CanAttack(this.state.selected))
		{
			this.state.isAttacking = !this.state.isAttacking;
		}
	}

	public void processEndTouch()
	{
		this.state.EndTurn();
	}

	public void processMoveTouch()
	{
		this.state.isAttacking = false;
		this.state.isUsingAbility = false;
		if (this.state.CanMove())
		{
			this.state.isMoving = !this.state.isMoving;
		}
	}

	public void processTouch(final float x, final float y) throws IOException
	{
		if (this.state.currentPlayer != 0)
		{
			return;
		}

		if ((x >= this.xOffset) && (x < (this.xOffset + this.largeButtonWidth)))
		{
			if ((y >= this.yOffset) && (y < (this.yOffset + this.height)))
			{
				this.processMoveTouch();
			}
		}
		if ((x >= (this.xOffset + this.largeButtonWidth)) && (x < (this.xOffset + (2 * this.largeButtonWidth))))
		{
			if ((y >= this.yOffset) && (y < (this.yOffset + this.height)))
			{
				this.processAttackTouch();
			}
		}
		if ((x >= (this.xOffset + (2 * this.largeButtonWidth))) && (x < (this.xOffset + (3 * this.largeButtonWidth))))
		{
			if ((y >= this.yOffset) && (y < (this.yOffset + this.height)))
			{
				this.processAbilityTouch();
			}
		}
		if ((x >= (this.xOffset + (3 * this.largeButtonWidth))) && (x < Gdx.graphics.getWidth()))
		{
			if ((y >= this.yOffset) && (y < (this.yOffset + this.height)))
			{
				this.processEndTouch();
			}
		}
	}
}
