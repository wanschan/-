import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

# =====================================
# 空间网格
# =====================================
x_min, x_max = -120, 120
N = 4096

x = np.linspace(x_min, x_max, N)
dx = x[1] - x[0]

# =====================================
# 时间参数
# =====================================
dt = 0.03
frames = 500

# =====================================
# 势垒参数
# =====================================
V0 = 2.0
barrier_width = 10

V = np.zeros_like(x)
V[np.abs(x) < barrier_width] = V0

# =====================================
# 初始高斯波包
# =====================================
x0 = -60
sigma = 10
k0 = 1.5

psi = (
    np.exp(-(x - x0)**2 / (2 * sigma**2))
    * np.exp(1j * k0 * x)
)

# 归一化
psi /= np.sqrt(np.sum(np.abs(psi)**2) * dx)

# =====================================
# FFT 动量空间
# =====================================
k = 2 * np.pi * np.fft.fftfreq(N, d=dx)

# 分裂算符法
expV_half = np.exp(-1j * V * dt / 2)
expK = np.exp(-1j * (k**2 / 2) * dt)

# =====================================
# 绘图
# =====================================
fig, ax = plt.subplots(figsize=(12, 5), facecolor='black')
ax.set_facecolor('black')

# 蓝色波函数实部
line_real, = ax.plot([], [], lw=2, color='#33AAFF')

# 势垒
ax.fill_between(
    x,
    -1.2,
    V / V0 * 2.4 - 1.2,
    color='gray',
    alpha=0.35
)

# 坐标设置
ax.set_xlim(-100, 100)
ax.set_ylim(-1.2, 1.2)

# 去除坐标轴
ax.set_xticks([])
ax.set_yticks([])

for spine in ax.spines.values():
    spine.set_visible(False)

# 标题
title = ax.text(
    0.5,
    1.03,
    "Quantum Tunneling",
    color='white',
    fontsize=18,
    ha='center',
    transform=ax.transAxes
)

# =====================================
# 初始化
# =====================================
def init():
    line_real.set_data([], [])
    return line_real,

# =====================================
# 时间演化
# =====================================
def evolve(psi):
    # 势能半步
    psi = expV_half * psi

    # 动能一步
    psi_k = np.fft.fft(psi)
    psi_k *= expK
    psi = np.fft.ifft(psi_k)

    # 势能半步
    psi = expV_half * psi

    return psi

# =====================================
# 动画更新
# =====================================
def update(frame):
    global psi

    # 多次迭代让动画更流畅
    for _ in range(2):
        psi = evolve(psi)

    # 实部
    y = np.real(psi)

    line_real.set_data(x, y)

    return line_real,

# =====================================
# 创建动画
# =====================================
ani = FuncAnimation(
    fig,
    update,
    frames=frames,
    init_func=init,
    interval=20,
    blit=True
)

plt.tight_layout()
plt.show()

# 保存视频：
# ani.save("quantum_tunneling_realpart.mp4", fps=60, dpi=200)