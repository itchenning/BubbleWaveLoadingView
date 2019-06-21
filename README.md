# 一个带有气泡效果的水波纹加载进度条
---

## 基于以下两个项目修改，感谢两位作者

[WaveLoadingView](https://github.com/tangqi92/WaveLoadingView)

[雕虫晓技(十) Android超简单气泡效果](https://www.gcssloop.com/gebug/bubble-sample)


# 效果

![](screenshot.gif)


# 用法

## xml中：

	<com.terry.bubblewaveloadingview.view.BubbleWaveLoadingView
		android:id="@+id/bubblewaveloadingview"
	    android:layout_width="150dp"
	    android:layout_height="150dp"
	    app:wb_amplitude="60"
	    app:wb_bg_color="#FFFFFF"
	    app:wb_boder_width="3dp"
	    app:wb_border_color="#66C309F3"
	    app:wb_bubble_color="#33FFFFFF"
	    app:wb_bubble_max_radius="10dp"
	    app:wb_bubble_max_size="30"
	    app:wb_bubble_max_speed_y="5"
	    app:wb_bubble_min_radius="3dp"
	    app:wb_first_wave_color="#0000FF"
	    app:wb_progress="60"
	    app:wb_second_wave_color="#550000FF" />

## 代码中：

	BubbleWaveLoadingView bubbleWaveLoadingView = findViewById(R.id.bubblewaveloadingview);
    bubbleWaveLoadingView.setProgress(80);



# 属性介绍

<table>
<tr>
<td>
<b>
属性名
</b> 
</td>
<td>
<b>
描述
</b> 
</td>
<td>
<b>
默认值
</b> 
</td>
</tr>
<td>
wb_bg_color
</td>
<td>
圆背景颜色
</td>
<td bgcolor="#F0F0F0">
#FFF0F0F0
</td>
<tr>
<td>
wb_border_color
</td>
<td>
边框颜色
</td>
<td bgcolor="#FFFFFF">
#FFFFFFFF
</td>
</tr>
<tr>
<td>
wb_first_wave_color
</td>
<td>
前面水波纹的颜色
</td>
<td bgcolor="#4646F8">
#FF4646F8
</td>
</tr>
<tr>
<td>
wb_second_wave_color
</td>
<td>
后面水波纹的颜色
</td>
<td bgcolor=rgba(38,109,255,0.25)>
#66266DFF
</td>
</tr>
<tr>
<td>
wb_bubble_color
</td>
<td>
气泡颜色
</td>
<td bgcolor=rgba(255,255,255,0.13)>
#33FFFFFF
</td>
</tr>
<tr>
<td>
wb_boder_width
</td>
<td>
外边框宽
</td>
<td align="center">
6dp
</td>
</tr>
<tr>
<td>
wb_bubble_max_radius
</td>
<td>
气泡最大半径
</td>
<td align="center">
10dp
</td>
</tr>
<tr>
<td>
wb_bubble_min_radius
</td>
<td>
气泡最小半径
</td>
<td align="center">
2dp
</td>
</tr>
<tr>
<td>
wb_bubble_max_size
</td>
<td>
气泡最大数量
</td>
<td align="center">
30
</td>
</tr>
<tr>
<td>
wb_bubble_max_speed_y
</td>
<td>
气泡上升最大速度
</td>
<td align="center">
3
</td>
</tr>
<tr>
<td>
wb_progress
</td>
<td>
进度
</td>
<td align="center">
60
</td>
</tr>
<tr>
<td>
wb_amplitude
</td>
<td>
水波振幅
</td>
<td align="center">
50f
</td>
</tr>
</table>