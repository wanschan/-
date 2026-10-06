from manim import *
import numpy as np

class FullWaveAnimation(Scene):
    def construct(self):
        self.camera.background_color = BLACK
        axes = Axes(
            x_range=[-16, 16],
            y_range=[-1.4, 1.4],
            x_length=30,
            y_length=3.5,
            axis_config={"color": LIGHT_GRAY, "include_ticks": False},
            tips=False
        )
        axes.y_axis.set_opacity(0)
        self.add(axes)

        k = 2.5   # <<==== 调节这个控制波动多少，越大越密

        def real_curve(x):
            if abs(x) > 14:
                return 0
            return np.exp(-(x**2)/9) * np.cos(k * x)

        def imag_curve(x):
            if abs(x) > 14:
                return 0
            return np.exp(-(x**2)/9) * np.sin(k * x)

        def mod_square_curve(x):
            if abs(x) > 14:
                return 0
            re = np.exp(-(x**2)/9) * np.cos(k * x)
            im = np.exp(-(x**2)/9) * np.sin(k * x)
            return re**2 + im**2

        red_wave = axes.plot(real_curve, x_range=[-22, 22], color=RED, stroke_width=3)
        blue_wave = axes.plot(imag_curve, x_range=[-22, 22], color=BLUE, stroke_width=3)
        mod2_wave = axes.plot(mod_square_curve, x_range=[-22, 22], color=WHITE, stroke_width=3)

        self.play(
            Create(red_wave, run_time=6),
            Create(blue_wave, run_time=6)
        )
        self.wait(2)
        self.play(Create(mod2_wave, run_time=4))
        self.wait(3)