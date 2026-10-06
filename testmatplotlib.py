
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from matplotlib.animation import FuncAnimation
from matplotlib import cm
import matplotlib.font_manager as fm












# ==================== 中文字体修复 ====================
def fix_chinese_font():
    """修复中文显示问题"""
    # 方法1：尝试常见中文字体
    chinese_fonts = ['Microsoft YaHei', 'SimHei', 'SimSun', 'KaiTi', 
                     'PingFang SC', 'STHeiti', 'WenQuanYi Micro Hei']
    
    for font in chinese_fonts:
        try:
            # 检查字体是否存在
            test_font = fm.FontProperties(family=font)
            if test_font.get_name() != 'DejaVu Sans':
                plt.rcParams['font.sans-serif'] = [font]
                plt.rcParams['axes.unicode_minus'] = False
                print(f"✓ 使用中文字体: {font}")
                return True
        except:
            continue
    
    # 方法2：查找系统中所有可能的中文字体
    import platform
    system = platform.system()
    
    if system == 'Windows':
        # Windows 系统字体目录
        font_dirs = ['C:\\Windows\\Fonts']
        font_patterns = ['msyh', 'simhei', 'simsun', 'simkai']
    elif system == 'Darwin':  # macOS
        font_dirs = ['/System/Library/Fonts', '/Library/Fonts']
        font_patterns = ['PingFang', 'STHeiti', 'STSong']
    else:  # Linux
        font_dirs = ['/usr/share/fonts', '/usr/local/share/fonts']
        font_patterns = ['WenQuanYi', 'NotoSansCJK', 'DroidSansFallback']
    
    for font_dir in font_dirs:
        if not any(os.path.exists(font_dir) for _ in [1]):
            continue
        for pattern in font_patterns:
            try:
                fonts = [f for f in fm.findSystemFonts(fontpaths=[font_dir]) 
                        if pattern.lower() in os.path.basename(f).lower()]
                if fonts:
                    fm.fontManager.addfont(fonts[0])
                    font_name = fm.FontProperties(fname=fonts[0]).get_name()
                    plt.rcParams['font.sans-serif'] = [font_name]
                    plt.rcParams['axes.unicode_minus'] = False
                    print(f"✓ 使用中文字体: {font_name}")
                    return True
            except:
                continue
    
    print("⚠ 未找到中文字体，部分标签将显示为英文")
    return False

import os
fix_chinese_font()

# 如果你的 matplotlib 版本较新，还需要设置
plt.rcParams['font.family'] = 'sans-serif'

# ==================== 参数设置 ====================
# 空间范围
x_min, x_max = -1.5, 1.5
z_min, z_max = -1.0, 1.0
nx, nz = 200, 80  # 减少分辨率以加快速度
x = np.linspace(x_min, x_max, nx)
z = np.linspace(z_min, z_max, nz)
X, Z = np.meshgrid(x, z)

# 势垒参数
V0 = 60.0           # 势垒高度
x0_barrier = 0.0    # 势垒中心
width_barrier = 0.6 # 势垒宽度

# 波包参数（能量 E < V0，隧穿情况）
E = 40.0            # 粒子能量
k1 = np.sqrt(2 * E)           # 区域 I 波数
kappa = np.sqrt(2 * (V0 - E)) # 区域 II 衰减常数

# 透射和反射系数（方势垒解析解）
def transmission_coefficient(E, V0, a):
    if E > V0:
        k2 = np.sqrt(2 * (E - V0))
        k1 = np.sqrt(2 * E)
        T = 4 * k1**2 * k2**2 / (4 * k1**2 * k2**2 + (k1**2 - k2**2)**2 * np.sin(k2 * a)**2)
    else:
        k1 = np.sqrt(2 * E)
        kappa = np.sqrt(2 * (V0 - E))
        T = 4 * k1**2 * kappa**2 / (4 * k1**2 * kappa**2 + (k1**2 + kappa**2)**2 * np.sinh(kappa * a)**2)
    return T

def reflection_coefficient(E, V0, a):
    return 1 - transmission_coefficient(E, V0, a)

T = transmission_coefficient(E, V0, width_barrier)
R = reflection_coefficient(E, V0, width_barrier)
A_inc = 1.0          # 入射振幅
A_ref = np.sqrt(R)   # 反射振幅
A_trans = np.sqrt(T) # 透射振幅

# 势垒边界
barrier_left = x0_barrier - width_barrier/2
barrier_right = x0_barrier + width_barrier/2

print(f"入射能量 E = {E}")
print(f"势垒高度 V0 = {V0}")
print(f"透射系数 T = {T:.4f}")
print(f"反射系数 R = {R:.4f}")

# ==================== 各区域波函数 ====================
def incident_wave(x, z, t):
    """入射波: 向右传播"""
    psi = A_inc * np.cos(k1 * x - E * t)  # 实部
    return psi

def reflected_wave(x, z, t):
    """反射波: 向左传播"""
    psi = A_ref * np.cos(-k1 * x - E * t)
    return psi

def barrier_wave(x, z, t):
    """势垒内部: 指数衰减"""
    psi = np.zeros_like(x)
    mask = (x >= barrier_left) & (x <= barrier_right)
    x_rel = x[mask] - x0_barrier
    # 双曲函数形式的衰减
    amp = A_inc * (np.exp(-kappa * (x_rel + width_barrier/2)) + 
                    np.exp(kappa * (x_rel - width_barrier/2))) / 2
    psi[mask] = amp * np.cos(E * t)
    return psi

def transmitted_wave(x, z, t):
    """透射波: 向右传播"""
    psi = A_trans * np.cos(k1 * x - E * t)
    # 只在势垒右侧显著
    psi[x < barrier_right] *= 0.1
    return psi

def combined_wave(x, z, t):
    """总波函数"""
    psi = np.zeros_like(x)
    
    # 区域 I: x < barrier_left
    mask_I = x < barrier_left
    psi[mask_I] = (A_inc * np.cos(k1 * x[mask_I] - E * t) + 
                    A_ref * np.cos(-k1 * x[mask_I] - E * t))
    
    # 区域 II: 势垒内部
    mask_II = (x >= barrier_left) & (x <= barrier_right)
    x_rel = x[mask_II] - x0_barrier
    amp = A_inc * (np.exp(-kappa * (x_rel + width_barrier/2)) + 
                    np.exp(kappa * (x_rel - width_barrier/2))) / 2
    psi[mask_II] = amp * np.cos(E * t)
    
    # 区域 III: x > barrier_right
    mask_III = x > barrier_right
    psi[mask_III] = A_trans * np.cos(k1 * x[mask_III] - E * t)
    
    # 边界处平滑
    return psi

# ==================== 颜色映射配置 ====================
# 使用不同的颜色映射代替纯色
wave_configs = [
    ('Incident Wave\n(入射波)', incident_wave, 'Blues'),
    ('Reflected Wave\n(反射波)', reflected_wave, 'Greens'),
    ('Barrier Interior\n(势垒内部衰减波)', barrier_wave, 'Reds'),
    ('Transmitted Wave\n(透射波)', transmitted_wave, 'Purples'),
    ('Combined Wave\n(合成波)', combined_wave, 'viridis')
]

# ==================== 创建 3D 图形 ====================
fig = plt.figure(figsize=(16, 10))

axes = []
surface_plots = []

for i, (title, wave_func, cmap_name) in enumerate(wave_configs):
    ax = fig.add_subplot(2, 3, i+1, projection='3d')
    axes.append(ax)
    ax.set_title(title, fontsize=12, fontweight='bold')
    ax.set_xlabel('x', fontsize=10)
    ax.set_ylabel('z', fontsize=10)
    ax.set_zlabel('ψ(x,z,t)', fontsize=10)
    ax.set_xlim(x_min, x_max)
    ax.set_ylim(z_min, z_max)
    ax.set_zlim(-1.2, 1.2)
    ax.view_init(elev=30, azim=-65)
    
    # 绘制势垒区域（半透明红色方块）
    y_mid = (z_min + z_max) / 2
    # 使用bar3d绘制势垒指示
    ax.bar3d(barrier_left, y_mid, -1.2, width_barrier, 0.05, 2.4, 
             alpha=0.25, color='red', shade=True)
    
    # 绘制势垒边界虚线
    ax.plot([barrier_left, barrier_left], [z_min, z_max], [-1.2, -1.2], 
            'r--', alpha=0.5, linewidth=1)
    ax.plot([barrier_right, barrier_right], [z_min, z_max], [-1.2, -1.2], 
            'r--', alpha=0.5, linewidth=1)
    
    # 初始表面（全零）
    surf = ax.plot_surface(X, Z, np.zeros_like(X), cmap=cmap_name, alpha=0.85,
                           vmin=-1, vmax=1, rstride=2, cstride=2, antialiased=True)
    surface_plots.append(surf)

# 第6个子图显示参数信息
ax_info = fig.add_subplot(2, 3, 6)
ax_info.axis('off')
info_text = f"""量子隧穿参数
{'='*25}

入射能量 E     = {E:.1f}
势垒高度 V₀    = {V0:.1f}
势垒宽度 a     = {width_barrier:.2f}
波数 k₁        = {k1:.2f}
衰减常数 κ     = {kappa:.2f}

透射系数 T     = {T:.4f}
反射系数 R     = {R:.4f}

条件: E < V₀
区域: 隧穿区域

颜色映射说明:
{'─'*25}
■ Blues      - 入射波
■ Greens     - 反射波
■ Reds       - 势垒内部
■ Purples    - 透射波
■ viridis    - 合成波

势垒位置: [{barrier_left:.1f}, {barrier_right:.1f}]
"""
ax_info.text(0.1, 0.95, info_text, transform=ax_info.transAxes, 
             fontsize=10, verticalalignment='top', fontfamily='monospace')
ax_info.set_title("Parameter Information", fontsize=12, fontweight='bold')

plt.tight_layout()

# ==================== 动画更新函数 ====================
def update(frame):
    t = frame * 0.04  # 时间步长
    for i, (ax, (title, wave_func, cmap_name), surf) in enumerate(zip(axes, wave_configs, surface_plots)):
        # 计算当前波函数值
        Z_vals = wave_func(X, Z, t)
        
        # 移除旧的表面
        surf.remove()
        
        # 绘制新的表面
        new_surf = ax.plot_surface(X, Z, Z_vals, cmap=cmap_name, alpha=0.85,
                                    vmin=-1, vmax=1, rstride=2, cstride=2, 
                                    antialiased=True)
        surface_plots[i] = new_surf
        
        # 更新标题显示时间
        ax.set_title(f"{title.split(chr(10))[0]} | t = {t:.2f}", fontsize=10, fontweight='bold')
    
    return surface_plots

# 创建动画（减少帧数以提高性能）
print("正在生成动画...")
ani = FuncAnimation(fig, update, frames=60, interval=50, blit=False, repeat=True)

plt.tight_layout()
plt.show()

# 可选：保存动画
# ani.save('quantum_tunneling_3d.gif', writer='pillow', fps=20)
# print("动画已保存为 quantum_tunneling_3d.gif")