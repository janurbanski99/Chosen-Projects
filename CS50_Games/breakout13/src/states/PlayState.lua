--[[
    GD50
    Breakout Remake

    -- PlayState Class --

    Author: Colton Ogden
    cogden@cs50.harvard.edu

    Represents the state of the game in which we are actively playing;
    player should control the paddle, with the ball actively bouncing between
    the bricks, walls, and the paddle. If the ball goes below the paddle, then
    the player should lose one point of health and be taken either to the Game
    Over screen if at 0 health or the Serve screen otherwise.
]]

PlayState = Class{__includes = BaseState}

--[[
    We initialize what's in our PlayState via a state table that we pass between
    states as we go from playing to serving.
]]
function PlayState:enter(params)
    self.paddle = params.paddle
    self.bricks = params.bricks
    self.health = params.health
    self.score = params.score
    self.highScores = params.highScores
    self.ball = params.ball
    self.level = params.level

    self.recoverPoints = 1000
    self.powerup = Powerup()

    self.timer = love.timer.getTime()

    self.balls = {self.ball}
end

function PlayState:update(dt)
    if self.paused then
        if love.keyboard.wasPressed('space') then
            self.paused = false
            gSounds['pause']:play()
        else
            return
        end
    elseif love.keyboard.wasPressed('space') then
        self.paused = true
        gSounds['pause']:play()
        return
    end

    -- update positions based on velocity
    self.paddle:update(dt)

    -- if self.ball then
    --     self.ball:update(dt)
    -- end
    
    -- if self.ball2 then
    --     self.ball2:update(dt)
    -- end

    for _, ball in ipairs(self.balls) do
        ball:update(dt)
    end

    if self.powerup.inPlay then
        self.powerup:update(dt)
    end

    if self.powerup.y > 260 then
        self.powerup.inPlay = false
        self.powerup = Powerup()
    end

    if love.timer.getTime() - self.timer >= 5 and Ball.instanceCount < 2 then
        self.powerup.inPlay = true
        self.timer = love.timer.getTime()
    end

    for _, ball in ipairs(self.balls) do
        if ball:collides(self.paddle) then
            -- raise ball above paddle in case it goes below it, then reverse dy
            ball.y = self.paddle.y - 8
            ball.dy = -ball.dy

            --
            -- tweak angle of bounce based on where it hits the paddle
            --

            -- if we hit the paddle on its left side while moving left...
            if ball.x < self.paddle.x + (self.paddle.width / 2) and self.paddle.dx < 0 then
                ball.dx = -50 + -(8 * (self.paddle.x + self.paddle.width / 2 - ball.x))
            
            -- else if we hit the paddle on its right side while moving right...
            elseif ball.x > self.paddle.x + (self.paddle.width / 2) and self.paddle.dx > 0 then
                ball.dx = 50 + (8 * math.abs(self.paddle.x + self.paddle.width / 2 - ball.x))
            end

            gSounds['paddle-hit']:play()
        end
    end

    if self.powerup:collides(self.paddle) then
        self.powerup.inPlay = false
        self.powerup = Powerup()
        self.ball2 = Ball()
        self.ball3 = Ball()
        table.insert(self.balls, self.ball2)
        table.insert(self.balls, self.ball3)
        self.ball2.skin = math.random(7)
        self.ball2.x = self.paddle.x + (self.paddle.width / 2) - 4
        self.ball2.y = self.paddle.y - 8
        self.ball3.skin = math.random(7)
        self.ball3.x = self.paddle.x + (self.paddle.width / 2) - 4
        self.ball3.y = self.paddle.y - 8
    end

    -- detect collision across all bricks with the ball
    
    for _, ball in ipairs(self.balls) do
        for k, brick in pairs(self.bricks) do

        -- only check collision if we're in play
        if brick.inPlay and ball:collides(brick) then

            -- add to score
            self.score = self.score + (brick.tier * 200 + brick.color * 25)

            -- trigger the brick's hit function, which removes it from play
            if isInstanceOf(brick, Brick) then
            brick:hit()
            end

            -- if we have enough points, recover a point of health
            if self.score > self.recoverPoints then
                -- can't go above 3 health
                if self.paddle.size < 4 then
                    self.recoverPoints = self.recoverPoints * 2
                    self.paddle:grow()
                end
                self.health = math.min(3, self.health + 1)

                -- multiply recover points by 2
                self.recoverPoints = math.min(100000, self.recoverPoints * 2)

                -- play recover sound effect
                gSounds['recover']:play()
            end

            -- go to our victory screen if there are no more bricks left
            if self:checkVictory() then
                gSounds['victory']:play()
                Ball.instanceCount = 0

                gStateMachine:change('victory', {
                    level = self.level,
                    paddle = self.paddle,
                    health = self.health,
                    score = self.score,
                    highScores = self.highScores,
                    ball = ball,
                    recoverPoints = self.recoverPoints
                })
            end

            --
            -- collision code for bricks
            --
            -- we check to see if the opposite side of our velocity is outside of the brick;
            -- if it is, we trigger a collision on that side. else we're within the X + width of
            -- the brick and should check to see if the top or bottom edge is outside of the brick,
            -- colliding on the top or bottom accordingly 
            --

            -- left edge; only check if we're moving right, and offset the check by a couple of pixels
            -- so that flush corner hits register as Y flips, not X flips
            if ball.x + 2 < brick.x and ball.dx > 0 then
                
                -- flip x velocity and reset position outside of brick
                ball.dx = -ball.dx
                ball.x = brick.x - 8
            
            -- right edge; only check if we're moving left, , and offset the check by a couple of pixels
            -- so that flush corner hits register as Y flips, not X flips
            elseif ball.x + 6 > brick.x + brick.width and ball.dx < 0 then
                
                -- flip x velocity and reset position outside of brick
                ball.dx = -ball.dx
                ball.x = brick.x + 32
            
            -- top edge if no X collisions, always check
            elseif ball.y < brick.y then
                
                -- flip y velocity and reset position outside of brick
                ball.dy = -ball.dy
                ball.y = brick.y - 8
            
            -- bottom edge if no X collisions or top collision, last possibility
            else
                
                -- flip y velocity and reset position outside of brick
                ball.dy = -ball.dy
                ball.y = brick.y + 16
            end

            -- slightly scale the y velocity to speed up the game, capping at +- 150
            if math.abs(ball.dy) < 150 then
                ball.dy = ball.dy * 1.02
            end

            -- only allow colliding with one brick, for corners
            break
        end
        end
    end

    -- if ball goes below bounds, revert to serve state and decrease health
    
    for _, ball in ipairs(self.balls) do
        if ball.y >= VIRTUAL_HEIGHT then
            gSounds['hurt']:play()
            -- ball:storeSpeed()
            ball:destroy()
            self.timer = love.timer.getTime()
            if ball == self.ball then
                table.remove(self.balls, 1)
                self.ball = self.balls[1]
                self.ball2 = self.balls[2]
                -- self.ball2 = null
            elseif ball == self.ball2 then
                table.remove(self.balls, 2)
                self.ball = self.balls[1]
                self.ball2 = self.balls[2]
                -- self.ball2 = null
            -- ball:restoreSpeed()
            elseif ball == self.ball3 then
                table.remove(self.balls, 3)
            end

            if Ball.instanceCount == 0 then
                self.health = self.health - 1
                if self.paddle.size ~= 1 then
                    self.paddle:shrink()
                end

                if self.health == 0 then
                    gStateMachine:change('game-over', {
                        score = self.score,
                        highScores = self.highScores
                    })
                else
                    gStateMachine:change('serve', {
                        paddle = self.paddle,
                        bricks = self.bricks,
                        health = self.health,
                        score = self.score,
                        highScores = self.highScores,
                        level = self.level,
                        recoverPoints = self.recoverPoints
                    })
                end
            end
        end
    end

    -- for rendering particle systems
    for k, brick in pairs(self.bricks) do
        brick:update(dt)
    end

    if love.keyboard.wasPressed('escape') then
        love.event.quit()
    end
end

function PlayState:render()
    local y = 50
    for _, ball in ipairs(self.balls) do
        love.graphics.print('Ball: ' .. tostring(ball), 5, y)
        y = y + 15
        love.graphics.print('Ball: ' .. tostring(ball.dy), 5, y)
        y = y + 15
    end
    love.graphics.print('InstanceCount: ' .. tostring(Ball.instanceCount), 5, y)

    self.powerup:render()

    -- render bricks
    for k, brick in pairs(self.bricks) do
        brick:render()
    end

    -- render all particle systems
    for k, brick in pairs(self.bricks) do
        brick:renderParticles()
    end

    self.paddle:render()

    for _, ball in ipairs(self.balls) do
        ball:render()
    end

    renderScore(self.score)
    renderHealth(self.health)

    -- pause text, if paused
    if self.paused then
        love.graphics.setFont(gFonts['large'])
        love.graphics.printf("PAUSED", 0, VIRTUAL_HEIGHT / 2 - 16, VIRTUAL_WIDTH, 'center')
    end
end

function PlayState:checkVictory()
    for k, brick in pairs(self.bricks) do
        if brick.inPlay then
            return false
        end 
    end

    return true
end