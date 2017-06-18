#
# Regular cron jobs for the fastmath package
#
0 4	* * *	root	[ -x /usr/bin/fastmath_maintenance ] && /usr/bin/fastmath_maintenance
