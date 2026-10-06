# -*- coding: utf-8 -*-
from manim import *
import numpy as np


# ===============================================================
# 1. Physics and Numerical: 1D Time-Dependent Schrodinger Equation
# ===============================================================


def generate_spatial_grid(num_points: int, domain_half_length: float) -> tuple[np.ndarray, float]:
    x = np.linspace(-domain_half_length, domain_half_length, num_points, endpoint=False)
    dx = x[1] - x[0]
    return x, dx


def generate_wavenumbers(num_points: int, dx: float) -> np.ndarray:
    freqs = np.fft.fftfreq(num_points, d=dx)
    k = 2.0 * np.pi * freqs
    return k


def build_potential_barrier(x: np.ndarray, barrier_left: float, barrier_right: float, barrier_height: float) -> np.ndarray:
    V = np.zeros_like(x)
    mask = (x >= barrier_left) & (x <= barrier_right)
    V[mask] = barrier_height
    return V


def build_initial_wave_packet_from_left(x: np.ndarray, k0: float, amplitude: float = 1.0, 
                                       packet_center: float = -30.0, packet_width: float = 3.0) -> np.ndarray:
    gaussian_envelope = np.exp(-((x - packet_center) ** 2) / (2.0 * (packet_width ** 2)))
    plane_wave_phase = np.exp(1j * k0 * x)
    psi = amplitude * gaussian_envelope * plane_wave_phase
    return psi


def split_step_fourier_time_evolution(
    psi0: np.ndarray,
    potential: np.ndarray,
    time_step_sim: float,
    num_sim_steps: int,
    dx: float,
    k_array: np.ndarray,
    store_stride: int,
) -> tuple[np.ndarray, np.ndarray]:
    num_points = psi0.size

    exp_halfV = np.exp(-1j * potential * (time_step_sim / 2.0))
    expT = np.exp(-1j * (k_array ** 2) * (time_step_sim / 2.0))

    psi = psi0.astype(np.complex128, copy=True)

    num_saved = (num_sim_steps // store_stride) + 1
    psi_saved = np.zeros((num_saved, num_points), dtype=np.complex128)
    times_saved = np.zeros(num_saved, dtype=np.float64)

    save_index = 0
    psi_saved[save_index, :] = psi
    times_saved[save_index] = 0.0
    save_index += 1

    for step in range(1, num_sim_steps + 1):
        psi *= exp_halfV
        psi_k = np.fft.fft(psi)
        psi_k *= expT
        psi = np.fft.ifft(psi_k)
        psi_k = np.fft.fft(psi)
        psi_k *= expT
        psi = np.fft.ifft(psi_k)
        psi *= exp_halfV

        if (step % store_stride) == 0:
            psi_saved[save_index, :] = psi
            times_saved[save_index] = step * time_step_sim
            save_index += 1

    psi_saved = psi_saved[:save_index, :]
    times_saved = times_saved[:save_index]
    return times_saved, psi_saved


# ===============================================================
# 2. Manim Scene: Quantum Tunneling Animation
# ===============================================================


class QuantumBarrierInfinity(Scene):
    def construct(self) -> None:

        # Standard 16:9 aspect ratio
        self.camera.frame_width = 16.0
        self.camera.frame_height = 9.0
        self.camera.pixel_width = 1920
        self.camera.pixel_height = 1080

        # ---------- Numerical Parameters (optimized for speed) ----------
        num_points = 2048          # Reduced from 4096 for faster computation
        L = 100.0
        x, dx = generate_spatial_grid(num_points, L)
        k = generate_wavenumbers(num_points, dx)

        barrier_left = -7.0
        barrier_right = 7.0
        barrier_height = 2.0        
        V = build_potential_barrier(x, barrier_left, barrier_right, barrier_height)

        k0 = 1.5
        amplitude = 0.5
        packet_center = -30.0
        packet_width = 3.0
        psi0 = build_initial_wave_packet_from_left(x, k0, amplitude, packet_center, packet_width)

        t_max = 20.0
        dt_sim = 1e-3
        total_steps = int(t_max / dt_sim)

        target_fps = 60.0
        dt_store = 1.0 / target_fps
        store_stride = max(1, int(round(dt_store / dt_sim)))

        # ---------- Precompute Time Evolution ----------
        times, psi_saved = split_step_fourier_time_evolution(
            psi0=psi0,
            potential=V,
            time_step_sim=dt_sim,
            num_sim_steps=total_steps,
            dx=dx,
            k_array=k,
            store_stride=store_stride,
        )
        density_saved = (np.abs(psi_saved) ** 2).astype(np.float64)
        real_saved = np.real(psi_saved).astype(np.float64)
        imag_saved = np.imag(psi_saved).astype(np.float64)

        density_max = float(np.max(density_saved))
        y_max = max(0.3, min(0.8, density_max * 1.3))

        # ---------- Build Axes and Graphics ----------
        display_range = 25.0
        
        axes = Axes(
            x_range=[-display_range, display_range, 5.0],
            y_range=[0.0, y_max, y_max / 5.0],
            x_length=14.0,
            y_length=6.0,
            axis_config={
                "include_numbers": True,
                "stroke_width": 3,
                "font_size": 24,
                "decimal_number_config": {"num_decimal_places": 2},
            },
            tips=False,
        ).to_edge(DOWN, buff=0.5)

        # Axis labels
        x_label = MathTex("x", font_size=32).next_to(axes.get_x_axis(), DOWN, buff=0.3)
        y_label = MathTex("|\\psi|^2", font_size=32).next_to(axes.get_y_axis(), LEFT, buff=0.3)
        
        # Potential Barrier
        barrier_height_display = barrier_height * 0.3
        barrier_polygon = Polygon(
            axes.c2p(barrier_left, 0.0, 0.0),
            axes.c2p(barrier_right, 0.0, 0.0),
            axes.c2p(barrier_right, barrier_height_display, 0.0),
            axes.c2p(barrier_left, barrier_height_display, 0.0),
            fill_color="#87CEEB",
            fill_opacity=0.5,
            stroke_width=0,
        )

        # Dashed lines
        dashed_left = DashedLine(
            axes.c2p(barrier_left, 0, 0),
            axes.c2p(barrier_left, y_max, 0),
            color=YELLOW,
            stroke_width=2,
            dash_length=0.1,
        )
        dashed_right = DashedLine(
            axes.c2p(barrier_right, 0, 0),
            axes.c2p(barrier_right, y_max, 0),
            color=YELLOW,
            stroke_width=2,
            dash_length=0.1,
        )

        # 已删除势垒内部文字标签 "Barrier"
        # barrier_label = Text("Barrier", font_size=24, color=BLUE_C).next_to(
        #     axes.c2p(0, barrier_height_display + 0.15, 0), UP
        # )

        left_label = Text("Left", font_size=20, color=YELLOW).next_to(
            axes.c2p(barrier_left - 1.5, y_max/2, 0), ORIGIN
        )
        right_label = Text("Right", font_size=20, color=YELLOW).next_to(
            axes.c2p(barrier_right + 1.5, y_max/2, 0), ORIGIN
        )

        time_tracker = ValueTracker(0.0)
        x_values = x

        # Helper to get current index and subsample for speed
        def get_data(y_data):
            current_t = time_tracker.get_value()
            max_index = len(times) - 1
            dt_store_effective = times[1] - times[0] if len(times) > 1 else 1.0 / 60.0
            index = int(np.clip(round(current_t / dt_store_effective), 0, max_index))
            y_values = y_data[index]
            mask = (x >= -display_range) & (x <= display_range)
            x_display = x[mask]
            y_display = y_values[mask]
            # Subsample for rendering speed (reduce points)
            step = 2
            return x_display[::step], y_display[::step]

        # Probability Density Curve
        def build_density_graph():
            x_disp, y_disp = get_data(density_saved)
            graph = axes.plot_line_graph(
                x_values=x_disp,
                y_values=y_disp,
                add_vertex_dots=False,
                line_color=WHITE,
                stroke_width=3,
            )
            return graph

        density_graph = always_redraw(build_density_graph)

        # Fill under density
        def build_fill_graph():
            x_disp, y_disp = get_data(density_saved)
            points = [axes.c2p(x_disp[0], 0, 0)]
            for xi, yi in zip(x_disp, y_disp):
                points.append(axes.c2p(xi, yi, 0))
            points.append(axes.c2p(x_disp[-1], 0, 0))
            fill = Polygon(*points, color=WHITE, fill_opacity=0.08, stroke_opacity=0)
            return fill

        fill_graph = always_redraw(build_fill_graph)

        # Real part (Red)
        def build_real_graph():
            x_disp, y_disp = get_data(real_saved)
            scale_factor = y_max / (2.0 * np.max(np.abs(y_disp)) + 1e-10)
            y_disp_scaled = y_disp * scale_factor + y_max / 2
            graph = axes.plot_line_graph(
                x_values=x_disp,
                y_values=y_disp_scaled,
                add_vertex_dots=False,
                line_color=RED,
                stroke_width=2,
            )
            return graph

        real_graph = always_redraw(build_real_graph)

        # Imaginary part (Blue)
        def build_imag_graph():
            x_disp, y_disp = get_data(imag_saved)
            scale_factor = y_max / (2.0 * np.max(np.abs(y_disp)) + 1e-10)
            y_disp_scaled = y_disp * scale_factor + y_max / 2
            graph = axes.plot_line_graph(
                x_values=x_disp,
                y_values=y_disp_scaled,
                add_vertex_dots=False,
                line_color=BLUE,
                stroke_width=2,
            )
            return graph

        imag_graph = always_redraw(build_imag_graph)

        # Time display
        time_text = always_redraw(
            lambda: Text(
                f"t = {time_tracker.get_value():.1f}",
                font_size=28,
                color=WHITE
            ).move_to(np.array([-6.5, 3.8, 0]))
        )

        # Title
        title = Text("Quantum Tunneling", font_size=36, color=WHITE).to_edge(UP, buff=0.5)
        subtitle = Text("Wave Packet Scattering by a Barrier", font_size=24, color=GRAY).next_to(title, DOWN, buff=0.1)

        # Legend
        legend_real = Text("Re(ψ)", font_size=20, color=RED).move_to(np.array([6.5, 3.5, 0]))
        legend_imag = Text("Im(ψ)", font_size=20, color=BLUE).move_to(np.array([6.5, 3.0, 0]))
        legend_density = Text("|ψ|²", font_size=20, color=WHITE).move_to(np.array([6.5, 2.5, 0]))

        # ---------- Add all elements ----------
        self.add(
            axes, x_label, y_label,
            barrier_polygon,  # barrier_label 已删除
            dashed_left, dashed_right,
            left_label, right_label,
            fill_graph, density_graph,
            real_graph, imag_graph,
            time_text,
            title, subtitle,
            legend_real, legend_imag, legend_density,
        )
        
        # Play animation
        self.play(
            time_tracker.animate.set_value(float(times[-1])), 
            run_time=float(times[-1]), 
            rate_func=linear
        )
        self.wait(1)