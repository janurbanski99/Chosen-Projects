Powerup = Class{}

function Powerup:init(skin)
    self.width = 10
    self.height = 10

    self.x = 20 + math.random(VIRTUAL_WIDTH - 20)
    self.y = VIRTUAL_HEIGHT / 2 - 25

    self.dy = 30

    self.skin = math.random(10)

    self.inPlay = false
end

function Powerup:render()
    if self.inPlay then
        love.graphics.draw(gTextures['powerups'], gFrames['powerups'][self.skin],
            self.x, self.y, 0, self.width / 25, self.height / 25)
    end

    love.graphics.print('y: ' .. tostring(self.y), 5, 15)
end


function Powerup:update(dt)
    self.y = self.y + self.dy * dt
end

function Powerup:collides(target)
    if self.x > target.x + target.width or target.x > self.x + self.width then
        return false
    end
    if self.y > target.y + target.height or target.y > self.y + self.height then
        return false
    end 
    return true
end

