import numpy as np
import matplotlib.pyplot as plt

# ---------- 字体与数学字体设置 ----------
plt.rcParams['font.sans-serif'] = ['Microsoft YaHei', 'SimHei', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False
plt.rcParams['mathtext.fontset'] = 'dejavusans'   # 关键：避免中文缺 \u2212 字形

# ---------- 物理常数 ----------
m    = 9.109e-31
eV   = 1.602e-19
hbar = 1.0546e-34

V0 = 2.0 * eV
a  = 0.5e-9

gamma  = m * V0 * a**2 / (2 * hbar**2)
T_at_1 = 1.0 / (1.0 + gamma)

print(f"gamma = {gamma:.3f}")
print(f"T(E=V0) = {T_at_1:.4f} = {T_at_1:.2%}")

# ---------- 透射系数 ----------
def T_barrier(x, gamma):
    x = np.asarray(x, dtype=float)
    T = np.empty_like(x, dtype=float)
    lt = x < 1.0
    gt = x > 1.0
    eq = ~(lt | gt)

    if np.any(lt):
        xl = x[lt]
        arg = 2.0 * np.sqrt(gamma * (1.0 - xl))
        T[lt] = 1.0 / (1.0 + np.sinh(arg)**2 / (4.0 * xl * (1.0 - xl)))

    if np.any(gt):
        xg = x[gt]
        arg = 2.0 * np.sqrt(gamma * (xg - 1.0))
        T[gt] = 1.0 / (1.0 + np.sin(arg)**2 / (4.0 * xg * (xg - 1.0)))

    T[eq] = 1.0 / (1.0 + gamma)
    return T

# ---------- 数据 ----------
x_vals = np.concatenate([
    np.linspace(1e-4, 0.999, 800),
    [1.0],
    np.linspace(1.001, 1.2, 400)
])

T_vals  = T_barrier(x_vals, gamma)
T_small = T_barrier(x_vals, 1e-3)   # 弱势垒极限

x_point = 0.5
T_point = T_barrier(np.array([x_point]), gamma)[0]
print(f"T(E/V0=0.5) = {T_point:.4f} = {T_point:.2%}")

# ---------- 画图 ----------
fig, ax = plt.subplots(figsize=(7, 5), constrained_layout=True)

ax.plot(x_vals, T_vals,  color='C0',   lw=2.5,
        label=f'本题参数 $\\gamma={gamma:.2f}$')
ax.plot(x_vals, T_small, color='gray', lw=2,
        label='$V_0a^2\\to 0$ 极限（灰线）')

ax.axvline(1.0,    color='k', ls='--', lw=1, alpha=0.8)
ax.axhline(T_at_1, color='k', ls=':',  lw=1, alpha=0.8)

# 注意：$...$ 里的 % 写成 \%
ax.annotate(
    f'$E/V_0=1$ 渐近值\n$T={T_at_1*100:.1f}\\%$',
    xy=(1.0, T_at_1),
    xytext=(0.45, 0.04),
    arrowprops=dict(arrowstyle='->', color='k'),
    fontsize=10
)

ax.scatter([x_point], [T_point], color='red', zorder=5)
ax.annotate(
    f'本题参数点\n$E/V_0=0.5$, $T={T_point*100:.2f}\\%$',
    xy=(x_point, T_point),
    xytext=(0.12, 2e-4),
    arrowprops=dict(arrowstyle='->', color='red'),
    color='red',
    fontsize=10
)

ax.annotate(
    r'$T\to 100\%$',
    xy=(0.6, 0.999),
    xytext=(0.75, 0.25),
    arrowprops=dict(arrowstyle='->', color='gray'),
    color='gray',
    fontsize=10
)

ax.set_yscale('log')
ax.set_xlim(0, 1.2)
ax.set_ylim(1e-6, 1.2)

ax.set_xlabel(r'$E/V_0$')
ax.set_ylabel(r'$T(E)$')
ax.set_title('矩形势垒透射系数')

ax.grid(True, which='both', ls=':', alpha=0.3)
ax.legend(loc='lower right', fontsize=9)

plt.savefig('transmission_barrier.png', dpi=300)
plt.show()