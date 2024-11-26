PauseState = Class{__includes = PlayState}

function PauseState:enter(params)
    self.pipePairs = params.pipePairs
    self.bird = params.bird
    self.timer = params.timer
    self.score = params.score
end


function PauseState:render()
    love.graphics.setFont(flappyFont)
    love.graphics.printf('PAUSE', 0, 64, VIRTUAL_WIDTH, 'center')

    love.graphics.setFont(mediumFont)
    love.graphics.printf('Press p to unpause', 0, 100, VIRTUAL_WIDTH, 'center')
    
    love.graphics.print(tostring(self.pipePairs), 8, 60)

end

function PauseState:update(dt)
    if love.keyboard.wasPressed('p') then
        gStateMachine:change('play', {
            pipePairs = self.pipePairs,
            bird = self.bird,
            timer = self.timer,
            score = self.score
        })
    end
end