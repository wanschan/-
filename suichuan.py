import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

# ==========================================
# 空间网格
# ==========================================
x_min, x_max = -200, 200
N = 4096

x = np.linspace(x_min, x_max, N)
dx = x[1] - x[0]

# ==========================================
# 时间参数
# ==========================================
dt = 0.05
frames = 450

# ==========================================
# 波包参数
# ==========================================
x0 = -90          # 初始位置
sigma = 12        # 波包宽度
k0 = 1.6          # 初始波数（动量）

# ==========================================
# 势垒参数
# ==========================================
V0 = 2.0
barrier_width = 10

V = np.zeros_like(x)
V[np.abs(x) < barrier_width] = V0

# ==========================================
# 初始波函数（高斯波包）
# ==========================================
psi = np.exp(
    -(x - x0)**2 / (2 * sigma**2)
) * np.exp(1j * k0 * x)

# 归一化
psi /= np.sqrt(np.sum(np.abs(psi)**2) * dx)

# ==========================================
# 动量空间
# ==========================================
k = 2 * np.pi * np.fft.fftfreq(N, d=dx)

# ==========================================
# 分裂算符法演化算符
# ==========================================
expV_half = np.exp(-1j * V * dt / 2)
expK = np.exp(-1j * (k**2 / 2) * dt)

# ==========================================
# 绘图
# ==========================================
fig, ax = plt.subplots(figsize=(12, 5), facecolor='black')

ax.set_facecolor('black')

# 波函数实部
line_real, = ax.plot(
    [],
    [],
    color='#33AAFF',
    lw=2,
    label='Re(ψ)'
)

# 势垒
barrier = V / V0 * 1.2

ax.fill_between(
    x,
    -1.5,
    barrier,
    color='gray',
    alpha=0.35
)

# 美化
ax.set_xlim(-150, 150)
ax.set_ylim(-1.5, 1.5)

ax.set_xticks([])
ax.set_yticks([])

for spine in ax.spines.values():
    spine.set_visible(False)

title = ax.text(
    0.5,
    1.03,
    "Quantum Tunneling",
    transform=ax.transAxes,
    ha='center',
    color='white',
    fontsize=18
)

# ==========================================
# 初始化
# ==========================================
def init():
    line_real.set_data([], [])
    return line_real,

# ==========================================
# 时间演化
# ==========================================
def evolve():
    global psi

    # 势能半步
    psi = expV_half * psi

    # 动能一步（FFT）
    psi_k = np.fft.fft(psi)
    psi_k *= expK
    psi = np.fft.ifft(psi_k)

    # 势能半步
    psi = expV_half * psi

# ==========================================
# 动画更新
# ==========================================
def update(frame):

    # 每帧多演化几次更平滑
    for _ in range(4):
        evolve()

    # 只显示实部
    y = np.real(psi)

    line_real.set_data(x, y)

    return line_real,

# ==========================================
# 动画
# ==========================================
ani = FuncAnimation(
    fig,
    update,
    frames=frames,
    init_func=init,
    interval=25,
    blit=True
)

plt.tight_layout()
plt.show()

# ==========================================
# 保存视频（可选）
# ==========================================
# ani.save("quantum_tunneling_realpart.mp4",
#          fps=30,
#          dpi=200)