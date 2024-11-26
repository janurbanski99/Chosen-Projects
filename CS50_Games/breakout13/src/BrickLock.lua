BrickLock = Class{__includes = Brick}

function BrickLock:render()
    if self.inPlay then
        love.graphics.draw(gTextures['main'], 
            -- multiply color by 4 (-1) to get our color offset, then add tier to that
            -- to draw the correct tier and color brick onto the screen
            gFrames['lockbrick'][1 + ((self.color - 1) * 4) + self.tier],
            self.x, self.y)
    end
end
