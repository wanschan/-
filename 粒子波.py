from manim import *
import numpy as np
import random

class MovingBallWithTrail(Scene):
    def construct(self):
        # Set black background
        self.camera.background_color = GREEN

        # Motion parameters
        amplitude = 5.0
        duration = 15  # Total duration for expansion
        
        # Create trail group
        trail_group = VGroup()
        
        # Create trail effect with expanding range
        def update_trail(mob, dt):
            current_time = self.renderer.time if hasattr(self, 'renderer') else 0
            
            # Create empty group
            new_trails = VGroup()
            
            # Calculate expansion progress (0 to 1 over the duration)
            expansion_progress = min(1, current_time / 12)  # Reach full expansion at 12 seconds
            
            # Current spread range: starts very small (0.1), expands to full amplitude (5.0)
            current_range = 0.1 + (amplitude - 0.1) * expansion_progress
            
            # Number of trails: start with few, increase to dense
            num_trails = int(20 + 130 * expansion_progress)  # 20 at start, 150 at full
            num_trails = min(num_trails, 200)
            
            # Generate random positions within the current range
            # More positions near center for density effect
            positions = []
            for _ in range(num_trails):
                # Use beta distribution to concentrate positions near center
                # This creates the "dense in middle, sparse at edges" effect
                if expansion_progress < 0.3:
                    # Early stage: more concentrated in center
                    r = np.random.beta(0.8, 2.0) * current_range
                else:
                    # Later stage: more spread out but still denser in center
                    r = np.random.beta(0.6, 2.0) * current_range
                
                # Random sign (left or right)
                if random.random() < 0.5:
                    x_pos = r
                else:
                    x_pos = -r
                
                positions.append(x_pos)
            
            # Create trails with position-dependent opacity
            for x_pos in positions:
                # Calculate distance from center
                distance_from_center = abs(x_pos)
                
                # Normalize distance by current range
                if current_range > 0:
                    normalized_distance = distance_from_center / current_range
                else:
                    normalized_distance = 0
                
                # Opacity: highest at center, decreases towards edges
                # Start with higher opacity, gradually become more transparent as range expands
                base_alpha = 0.5 - 0.45 * normalized_distance
                base_alpha = max(base_alpha, 0.02)
                
                # Scale opacity with expansion progress (early stages are brighter)
                opacity_scale = 0.5 + 0.5 * (1 - expansion_progress)
                alpha = base_alpha * opacity_scale
                
                # Add randomness
                alpha = alpha * random.uniform(0.6, 1.4)
                alpha = min(alpha, 0.6)
                alpha = max(alpha, 0.005)
                
                # Size: larger at center, smaller at edges
                base_radius = 0.12 - 0.09 * normalized_distance
                base_radius = max(base_radius, 0.02)
                
                # Scale size with expansion progress (start small, grow larger)
                size_scale = 0.3 + 0.7 * expansion_progress
                radius = base_radius * size_scale * random.uniform(0.6, 1.4)
                radius = min(radius, 0.15)
                radius = max(radius, 0.005)
                
                # Small random y-offset
                y_offset = random.uniform(-0.03, 0.03)
                
                trail_dot = Dot(
                    point=[x_pos, y_offset, 0],
                    radius=radius,
                    color=WHITE,
                    fill_opacity=alpha,
                    stroke_opacity=0
                )
                new_trails.add(trail_dot)
            
            # Replace old trails with new ones
            mob.become(new_trails)

        # Add updater to trail group
        trail_group.add_updater(update_trail)
        self.add(trail_group)

        # Run animation
        self.wait(duration)

        # Stop updates
        trail_group.remove_updater(update_trail)

        # Final display
        self.wait(2)