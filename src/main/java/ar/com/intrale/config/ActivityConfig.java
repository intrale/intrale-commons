package ar.com.intrale.config;

public class ActivityConfig {
	
    private Boolean enabled;
    private Integer maxInactivity;
    private String fixedDelay;
    private String initialDelay;
    
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Integer getMaxInactivity() {
		return maxInactivity;
	}
	public void setMaxInactivity(Integer maxInactivity) {
		this.maxInactivity = maxInactivity;
	}
	public String getFixedDelay() {
		return fixedDelay;
	}
	public void setFixedDelay(String fixedDelay) {
		this.fixedDelay = fixedDelay;
	}
	public String getInitialDelay() {
		return initialDelay;
	}
	public void setInitialDelay(String initialDelay) {
		this.initialDelay = initialDelay;
	}

}
